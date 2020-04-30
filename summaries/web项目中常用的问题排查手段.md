**第一类：CPU占用超高**

现象：程序运行一段时间后，cpu占用超高，系统极为卡顿，依然处于可用状态

```
处理方法：
1. 采用 top 命令，找出 CPU 占用最高的服务进程
2. 通过 ps -ef | grep PID 查看服务进程中的线程占用
3. 采用 jstack -l PID >> pid.log dump出堆栈信息
4. 采用 top -Hp PID 拿到占用 CPU 最高的线程信息
5. 采用 printf "%x\n" PID 将PID转为16进制的TID
6. 采用 grep TID -A20 pid.log 根据 TID 到堆栈信息中匹配出问题的代码块
```

**第二类：程序假死**

现象：初始时，页面无反馈，过一会，大量的接口调用失效，服务接近崩溃

```
处理方法：
1. 查看异常日志，如果出现大量的http请求wait，则可能是http接口调用未设置超时，大量的请求挂起后，把tomcat线程池耗尽(默认200个)。
　　一般重启之后会立马恢复正常，如此往复，建议设置超时时间3s；
2. 查看异常日志，依然是tomcat的wait，但是有设置超时，外部接口没问题，则很可能是自身程序的阻塞导致。
　　如果没有明确的异常日志导向，先直接查看数据库的慢sql(mysql数据库推荐)，或者查看数据库死锁(sqlserver数据库推荐)，找到卡顿主体，本质也是连接池耗尽；
3. 查看异常日志，程序抛出no connection，建议优化一下数据库的连接池配置，最小10，最大按照CPU核数的2-4倍，适宜线程最佳切换。
　　无论是mongoDB还是sql，均可先查看数据库连接数，酌情改大，然后再修改程序的连接；
程序假死的主因：某块业务将线程耗尽，导致整体业务无资源可用
```

**第三类：服务卡顿**

现象：页面打开缓慢或者接口调用耗时很长，已打开的服务，无明显卡顿

```
处理方法：
1. 在linux环境下，采用tomcat部署的web服务，通过top查看，发现cpu和内存占用无异常。
根据tomcat端口，查看当前并发连接数量：
netstat -ant | grep ESTABLISHED | grep 80 | wc -l
#netstat -an会打印系统当前网络链接状态，而grep ESTABLISHED 提取出已建立连接的信息。 然后wc -l统计。最终返回的数字就是当前所有80端口的已建立连接的总数。
2. 根据结果会发现，并发连接数超过了默认的配置,同时查看Apache的并发请求数及其TCP连接状态：
netstat -n | awk '/^tcp/ {++S[$NF]} END {for(a in S) print a, S[a]}'
# TIME_WAIT 8947 等待足够的时间以确保远程TCP接收到连接中断请求的确认
# FIN_WAIT1 15 等待远程TCP连接中断请求，或先前的连接中断请求的确认
# FIN_WAIT2 1 从远程TCP等待连接中断请求
# ESTABLISHED 55 代表一个打开的连接
# SYN_RECV 21 再收到和发送一个连接请求后等待对方对连接请求的确认
# CLOSING 2 没有任何连接状态
# LAST_ACK 4 等待原来的发向远程TCP的连接中断请求的确认
# 处理的最大并发请求数，默认值200 
max-threads: 250
# 在给定时间接受和处理的最大连接数，默认值10000 
max-connections: 10000 
# 初始化时创建的最小线程数，始终保持运行，默认值10 
min-spare-threads: 20 
# 监听端口队列最大数，满了之后客户请求会被拒绝(不能小于maxSpareThreads)，默认为100 
acceptCount: 700
3. 按照参数，建议设置并发连接数max-threads=250，acceptCount=700-1000，重启服务后，可以明显的提高访问量
配置文件在tomcat的conf目录下server.xml
<Connector port="80" maxHttpHeaderSize="8192"  
    maxThreads="250" minSpareThreads="20" maxSpareThreads="200"  
    enableLookups="false" redirectPort="8443" acceptCount="1000"  
    connectionTimeout="20000" disableUploadTimeout="true" />
4. 当tomcat并发用户量大的时候，单个jvm进程确实可能打开过多的文件句柄，需要查看文件句柄数
# 查看tomcat的进程ID，记录ID号，假设进程ID为10001
ps -ef |grep tomcat
#查看当前进程id为10001的 文件操作数
lsof -p 10001|wc -l  
# 查看每个用户允许打开的最大文件数，默认是1024   
ulimit -n
建议句柄数尽量调大：
1，临时生效，-H就是硬限制，加-S就是软限制，硬限制就是实际的限制，而软限制是警告限制，它只会给出警告
ulimit -SHn 10000
2. 永久生效，ulimits 的数值永久生效，必须修改配置文件/etc/security/limits.conf
echo "* soft nofile 204800"  >> /etc/security/limits.conf
echo "* hard nofile 204800"  >> /etc/security/limits.conf

echo "* soft nproc 204800"  >> /etc/security/limits.conf
echo "* hard nproc 204800 "  >> /etc/security/limits.conf
# * 表示所用的用户
3. 修改系统总限制，上面的修改都是对一个进程打开的文件句柄数量的限制，还需要设置系统的总限制，eg：设置进程打开的文件句柄数是1024，但是系统总线制才500，所以所有进程最多能打开文件句柄数量500
# 临时生效方法，重启机器后会失效：
echo 6553560 > /proc/sys/fs/file-max
# 永久生效方法，修改 /etc/sysctl.conf
echo fs.file-max = 6553560  >> /etc/sysctl.conf

如果按上述操作，依然无法改善。那原因可能是并发过大，单个请求耗时长，只能增加服务器，通过nginx分发请求。

实测tomcat的并发量，8核16U，单次请求300ms内，在250-300的时候运作良好，超过之后，访问开始出现较为明显的延时，并发在600-700时，延迟3s，并发到1000的时候，接近不可用。大量请求阻塞，优化tomcat的参数，和guava限流器，无好转，期间cpu和内存的占用一直不高。最后增加服务器，使用nginx分发，问题解决。
```

待续。。。

要内在形成一种方法论，问题出现后，能明晰可能出现问题的点，采用控制变量法，逐步处理，切勿乱打一气。

线上服务要做好日志和问题记录，每排查一次问题后，要注意总结和分享，将处理方案落实到项目的具体实现中，为自己也为别人栽树。　