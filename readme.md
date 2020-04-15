### 集合http，ftp，rabbitmq的文件与消息系列服务，归纳优化各种写法，整理出优雅的写法，提高程序性能好，学习之余给业务开发提供方便。
#### 环境搭建
##### 1. 使用IDEA开发，基于alibaba编程规范，配置编辑器的注释模块
######方法注释模板

````
template text:
**
 * description: $description$
 *
 * @param $param$
 * @return $return$
 */
 
param参数表达式：
groovyScript(
    "def commonResult=''; 
    def params=\"${_1}\".replaceAll('[\\\\[|\\\\]|\\\\s]', '').split(',').toList(); 
    for(i = 0; i < params.size(); i++) {
        if (i == 0) {
            commonResult += params[i];
        } else {
            commonResult += '\\n' + ' * @param ' + params[i];
        }
    }; 
    return commonResult ", methodParameters())
PS: 按照idea自定义的快捷键可以迅速生成方法注释，@throws的注释需要手动添加
````
##### 2. 在windows10本地创建新用户，搭建FTP服务器
##### 3. 在windows10本地创建新用户，搭建rabbitmq服务器
##### 4. 项目的resources/summaries中放着项目实践中的相关总结
#### 更新记录
##### 1. 2019-12-22 完成RestTemplate的服务开发，做了基本测试 
##### 2. 2019-12-23 完成HttpRestTemplate的服务开发，优化小文件下载，整合HTTP连接池,实现大文件的多线程分割下载，做了基本测试
##### 3. 2019-12-23 完成FTP服务的开发，整合FTP连接池，做了基本测试
##### 4. 2020-01-01 完成rabbitmq服务的开发，做了基本测试，分发模式的理解，可阅读官方文档[RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)
##### 5. 2020-01-02 参考[zhuma](https://github.com/zhumaer/zhuma)的博客规划了项目的日志打印和异常分类处理(非常值得借鉴)
##### 6. 2020-01-05 完成项目打包的总结，放在resources/summaries中
##### 7. 2020-01-12 完成spring hystrix和内置tomcat组件的参数调优的小结，放在resources/summaries中
##### 8. 2020-02-27 完成SSH服务和WebScoket服务的开发，使用自定义线程池，测试中
##### 9. 2020-03-13 开始使用logback的自定义线程日志sift特性，spring的close事件，整合quartz，实现通用的文件失败重传
##### 10. 2020-03-30 完成通用的文件失败重传，同步代码仓库到码云，自测试通过
##### 11. 2020-04-03 完成spring，mybatis，druid动态数据源切换，分布式id，可以访问监控页面：http://localhost:9012/druid/sql.html
##### 12. 2020-04-06 完成mybatis自动生成代码，实体注释
##### 13. 2020-04-12 完成项目的maven多模块改造
##### 14. 2020-04-14 开始项目的spring-cloud化，逐步引入功能

---
> 参考了众多博主的帖子，按照实际使用做了调整，一并感谢，共勉
> 1. [SpringBoot的restTemplate整合HttpClient连接池及配置](https://blog.csdn.net/zzzgd_666/article/details/88858181)
> 2. [Java8环境下使用restTemplate单/多线程下载大文件和小文件](https://blog.csdn.net/zzzgd_666/article/details/88915818)
> 3. [java.util.concurrent.ThreadFactory 实例讲解](https://blog.csdn.net/zombres/article/details/80497515)
> 4. [Springboot项目搭建有ftpClientPool的Ftp工具类](https://blog.csdn.net/u011424653/article/details/78637725/)
> 5. [git项目ftpClientPool](https://github.com/jellyflu/ftpClientPool)
> 6. [springboot快速启动插件ftp篇-连接池](https://blog.csdn.net/qq_31463999/article/details/82761938)
> 7. [Windows 下安装RabbitMQ服务器及基本配置](https://www.cnblogs.com/vaiyanzi/p/9531607.html)
> 8. [Restful Api写法心得之三《返回值篇》](https://blog.csdn.net/aiyaya_/article/details/78209992)
> 9. [Hystrix使用说明，配置参数说明](https://blog.csdn.net/tongtong_use/article/details/78611225)
> 10. [Java线程池最佳实践](https://blog.csdn.net/wanghao112956/article/details/99292107)
> 11. [SpringBoot与JUnit+Mockito 单元测试](https://www.tianmaying.com/tutorial/JunitForSpringBoot)
> 12. [一次logback多线程调优的经历](https://segmentfault.com/a/1190000016204970?utm_source=tag-newest)
> 13. [Logback - SiftingAppender](https://blog.csdn.net/tmdcda/article/details/87616919)
> 14. [自定义logback触发器策略进行日志滚动](https://www.oschina.net/question/5189_7691)
> 15. [spring-boot-2.0.3之quartz集成，最佳实践](https://www.cnblogs.com/youzhibing/p/10208056.html)
> 16. [Datasource动态切换](https://blog.csdn.net/qq_32078397/article/details/54694047)
> 17. [实现mybatis未知个数数据源动态切换](https://blog.csdn.net/CSDNOFZHC/article/details/90903786)
> 18. [mybatis拦截器设置分布式id(雪花算法id)](https://blog.csdn.net/qq_40250122/article/details/101535884)
> 19. [Native Operating System and Hardware Information](https://github.com/oshi/oshi)
> 20. [Mybatis generator生成工具简单介绍](https://www.cnblogs.com/zhouguanglin/p/11239583.html)
> 21. [springcloud项目搭建（Finchley.RELEASE版）](https://blog.csdn.net/qq_37170583/article/details/80704904)
<br>
***
待续...