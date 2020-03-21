package cn.henry.study.application;

/**
 * description: 文件或消息的失败重传机制
 * 1. 上传文件或发送消息失败时，将文件写入临时文件夹中，eg: temp_fail_data，同时将标记信息记录到缓存队列；
 * 发送成功的日志打印在send_success.log中，日志格式为:
 * {"rowKey":"used_by_service", "filePath":"real_path/testFile.txt"}
 * 2. 缓存队列为LinkedBlockingQueue bufferQueue(适用于频繁插入和删除，size=200)，存储对象为json，格式为:
 * {"rowKey":"used_by_service", "filePath":"temp_fail_data/testFailData.txt"}
 * 使用offer方法，如果队列已满，返回false，发送告警通知，直接记录失败的日志send_fail.log
 * 3. 定时任务(每5min执行一次)清空bufferQueue，拉取retryInitialQueue 200个，执行重传，循环success/fail流程
 * 先判断file是否存在，不存在直接丢弃；
 * 若存在，且success之后，删除temp_fail_data目录中的文件；
 * 4. 监听程序关闭，将bufferQueue的数据写到send_fail.log
 * 5. 程序启动初始化的时候，读取send_fail.log，初始化到retryInitialQueue, size=10000中，
 * 重命名send_fail.log -->send_fail_2020321215851.log
 *
 * @author Hlingoes
 * @date 2020/3/21 20:40
 */
public class DataFailRetryService {
    /**
     * 1. 自定义日志的appender，区分通用日志，传入DefaultFileService
     * 2. 具有失败重传功能的service，必须继承DefaultFileService
     * 3. 初始化的时候，通过DefaultFileService获取到实现类，缓存到两个HashMap中
     * DataFailCacheMap<Class.getName(), new ArrayBlockingQueue<json>(200)>,
     * DataRetryCacheMap<Class.getName(), new ArrayBlockingQueue<json>(10000)>，
     * 注册所有的retry服务，根据appender和Class.getName()获取xx_send_fail.log，初始化Map数据，并重名该日志
     * 4. service的send方法失败，打印常规日志后，抛出DataSendFailRetryException(service, json)
     * 5. 统一异常处理中，根据异常中的service和msg，获取Map缓存的List，走success/fail逻辑
     * 6. 启动定时任务，遍历CacheMap执行success/fail逻辑
     */
}
