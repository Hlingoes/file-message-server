package cn.henry.study.web.controller;

import cn.henry.study.web.anno.ResponseResult;
import cn.henry.study.common.result.CommonResult;
import cn.henry.study.web.service.rabbitmq.RabbitMqSend;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zxf
 * @消息队列提供者
 * @date 2019/8/15
 */
@RestController
@ResponseResult
public class RabbitMqController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqController.class);

    @Autowired
    private RabbitMqSend send;

    @ApiOperation(value = "简单模式发送json消息", notes = "简单模式发送json消息")
    @ApiImplicitParam(name = "msg", value = "消息json", required = true, dataType = "String", defaultValue = "test")
    @GetMapping("/simpleSend")
    public CommonResult simpleSend(@RequestParam(value = "msg", required = true) String msg) {
        this.send.simpleSend(msg);
        return CommonResult.success(msg);
    }

    @ApiOperation(value = "订阅模式发送json消息", notes = "订阅模式发送json消息")
    @ApiImplicitParam(name = "msg", value = "消息json", required = true, dataType = "String", defaultValue = "test")
    @GetMapping("/routeSend")
    public CommonResult routeSend(@RequestParam(value = "msg", required = true) String msg) {
        this.send.routeSend(msg);
        return CommonResult.success(msg);
    }

    @ApiOperation(value = "路由模式发送json消息", notes = "路由模式发送json消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msg", value = "消息json", required = true, dataType = "String", defaultValue = "test"),
            @ApiImplicitParam(name = "routingKey", value = "key", required = true, dataType = "String", defaultValue = "test")})
    @GetMapping("/routingSend")
    public CommonResult routingSend(
            @RequestParam(value = "msg", required = true) String msg,
            @RequestParam(value = "routingKey", required = true) String routingKey) {
        this.send.routingSend(routingKey, msg);
        return CommonResult.success(msg);
    }

    @ApiOperation(value = "主题模式发送json消息", notes = "主题模式发送json消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msg", value = "消息json", required = true, dataType = "String", defaultValue = "test"),
            @ApiImplicitParam(name = "routingKey", value = "key", required = true, dataType = "String", defaultValue = "test")})
    @GetMapping("/topicSend")
    public CommonResult topicSend(
            @RequestParam(value = "msg", required = true) String msg,
            @RequestParam(value = "routingKey", required = true) String routingKey) {
        this.send.topicSend(routingKey, msg);
        return CommonResult.success(msg);
    }

    @ApiOperation(value = "死信模式发送json消息", notes = "用于处理定时任务,如订单超时未支付自动取消")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msg", value = "消息json", required = true, dataType = "String", defaultValue = "test"),
            @ApiImplicitParam(name = "routingKey", value = "key", required = true, dataType = "String", defaultValue = "test")})
    @GetMapping("/beadSend")
    public CommonResult beadSend(
            @RequestParam(value = "msg", required = true) String msg,
            @RequestParam(value = "routingKey", required = true) String routingKey) {
        MessageProperties messageProperties = new MessageProperties();
        //设置消息过期时间,这里设置的时间是10分钟
        messageProperties.setExpiration(600 + "000");
        Message message = new Message(msg.getBytes(), messageProperties);
        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        //这里的key应该传死信队列绑定死信交换机的路由key,这里我们传key1
        this.send.beadSend("routing_key1", message);
        return CommonResult.success(msg);
    }

}
