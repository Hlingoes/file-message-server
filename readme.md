### 集合http，ftp，rabbitmq的文件与消息系列服务
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
    "def result=''; 
    def params=\"${_1}\".replaceAll('[\\\\[|\\\\]|\\\\s]', '').split(',').toList(); 
    for(i = 0; i < params.size(); i++) {
        if (i == 0) {
            result += params[i];
        } else {
            result += '\\n' + ' * @param ' + params[i];
        }
    }; 
    return result ", methodParameters())
PS: 按照idea自定义的快捷键可以迅速生成方法注释，@throws的注释需要手动添加
````
---
> 参考了众多博主的帖子，按照实际使用做了调整，一并感谢，共勉
> 1. [SpringBoot的restTemplate整合HttpClient连接池及配置](https://blog.csdn.net/zzzgd_666/article/details/88858181)
> 2. [Java8环境下使用restTemplate单/多线程下载大文件和小文件](https://blog.csdn.net/zzzgd_666/article/details/88915818)
> 3. [java.util.concurrent.ThreadFactory 实例讲解](https://blog.csdn.net/zombres/article/details/80497515)
<br>**** 待续...****