**集合http，ftp，rabbitmq的文件与消息系列服务**

****使用IDEA开发，基于alibaba编程规范，配置编辑器的注释模块****<br>
*******方法注释模板*******<br>

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
**** 待续...****