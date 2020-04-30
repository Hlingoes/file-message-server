#### springboot组件的常用参数解析
##### 1. springboot内置tomcat容器的参数配置
````
server:
    port: 12021
    # server端的socket超时间(毫秒)，使用值-1表示没有(即无限)超时，默认值为60000(即60秒)
    # Tomcat附带的标准server.xml将此值设置为20000(即20秒)，除非disableUploadTimeout设置为false，否则在读取请求正文(如果有)时也会使用此超时
    connection-timeout: 80000
    tomcat:
      # URL统一编码
      uri-encoding: UTF-8
      # 处理的最大并发请求数，默认值200
      max-threads: 1000        
      # 在给定时间接受和处理的最大连接数，默认值10000
      max-connections: 20000
      # 初始化时创建的最小线程数，始终保持运行，默认值10
      min-spare-threads: 20
      # 监听端口队列最大数，满了之后客户请求会被拒绝(不能小于maxSpareThreads)，默认为100
      acceptCount: 700
      # 取消post参数大小限制，默认为2097152(2M)
      max-http-post-size: -1 
      # 请求和响应HTTP标头的最大大小，以字节为单位指定，如果未指定，则此属性设置为8192(8 KB)
      max-http-header-size: 8192(8 KB)
````
开发中遇到问题，需查询tomcat官方文档: http://tomcat.apache.org/tomcat-8.0-doc/config/http.html#HTTP/1.1_and_HTTP/1.0_Support
##### 2. spring cloud hystrix的参数配置
````
hystrix.command.default和hystrix.threadpool.default中的default为默认CommandKey(默认值：当前执行方法名)
Execution相关的属性的配置：
隔离策略，有THREAD和SEMAPHORE
THREAD - 它在单独的线程上执行，并发请求受线程池中的线程数量的限制
SEMAPHORE - 它在调用线程上执行，并发请求受到信号量计数的限制
hystrix.command.default.execution.isolation.strategy 隔离策略，默认是Thread, 可选Thread｜Semaphore
在THREAD模式下，达到超时时间，可以中断
在SEMAPHORE模式下，会等待执行完成后，再去判断是否超时
设置标准：有retry，99meantime+avg meantime；没有retry，99.5meantime
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds 命令执行超时时间，默认1000ms
hystrix.command.default.execution.timeout.enabled 执行是否启用超时，默认启用true
hystrix.command.default.execution.isolation.thread.interruptOnTimeout 发生超时是是否中断，默认true(THREAD模式有效)
execution.isolation.thread.interruptOnCancel 当发生取消时，执行是否应该中断，默认值为false(THREAD模式有效)
hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests 最大并发请求数，默认10，该参数当使用ExecutionIsolationStrategy.SEMAPHORE策略时才有效。如果达到最大并发请求数，请求会被拒绝。理论上选择semaphore size的原则和选择thread size一致，但选用semaphore时每次执行的单元要比较小且执行速度快(ms级别)，否则的话应该用thread
semaphore应该占整个容器(tomcat)的线程池的一小部分

Fallback相关的属性:(应用于Hystrix的THREAD和SEMAPHORE策略)
hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests 如果并发数达到该设置值，请求会被拒绝和抛出异常并且fallback不会被调用，默认10
hystrix.command.default.fallback.enabled 当执行失败或者请求被拒绝，是否会尝试调用hystrixCommand.getFallback()，默认true

Collapser Properties相关参数:
hystrix.collapser.default.maxRequestsInBatch 单次批处理的最大请求数，达到该数量触发批处理，默认Integer.MAX_VALUE
hystrix.collapser.default.timerDelayInMilliseconds 触发批处理的延迟，也可以为创建批处理的时间＋该值，默认10
hystrix.collapser.default.requestCache.enabled 是否对HystrixCollapser.execute() and HystrixCollapser.queue()的cache，默认true

ThreadPool相关参数:
线程数默认值10适用于大部分情况(有时可以设置得更小)，如果需要设置得更大，那有个基本得公式可以follow：
requests per second at peak when healthy × 99th percentile latency in seconds + some breathing room
每秒最大支撑的请求数 (99%平均响应时间 + 缓存值)
比如：每秒能处理1000个请求，99%的请求响应时间是60ms，那么公式是：1000 * （0.060+0.012）
基本得原则时保持线程池尽可能小，主要是为了释放压力，防止资源被阻塞。当一切都是正常的时候，线程池一般仅会有1到2个线程激活来提供服务。
hystrix.threadpool.default.coreSize 并发执行的最大线程数，默认10
hystrix.threadpool.default.maxQueueSize BlockingQueue的最大队列数，当设为－1，会使用SynchronousQueue，值为正时使用LinkedBlcokingQueue。该设置只会在初始化时有效，之后不能修改threadpool的queue size，除非reinitialising thread executor。默认－1。
hystrix.threadpool.default.queueSizeRejectionThreshold 即使maxQueueSize没有达到，达到queueSizeRejectionThreshold该值后，请求也会被拒绝。因为maxQueueSize不能被动态修改，这个参数将允许我们动态设置该值。if maxQueueSize == -1，该字段将不起作用
hystrix.threadpool.default.keepAliveTimeMinutes 如果corePoolSize和maxPoolSize设成一样(默认实现)该设置无效。如果通过plugin（https://github.com/Netflix/Hystrix/wiki/Plugins）使用自定义实现，该设置才有用，默认1.
hystrix.threadpool.default.metrics.rollingStats.timeInMilliseconds 线程池统计指标的时间，默认10000
hystrix.threadpool.default.metrics.rollingStats.numBuckets 将rolling window划分为n个buckets，默认10
````
