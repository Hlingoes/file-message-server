package cn.henry.study.gateway.exception;

import cn.henry.study.common.result.CommonResult;
import cn.henry.study.common.result.ResultCode;
import io.netty.channel.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/4/25 21:40
 */
@Component
public class GateWayExceptionHandlerAdvice {
    private static Logger log = LoggerFactory.getLogger(GateWayExceptionHandlerAdvice.class);

    @ExceptionHandler(value = {ResponseStatusException.class})
    public CommonResult handle(ResponseStatusException ex) {
        log.error("response status exception:{}", ex.getMessage());
        return CommonResult.failure(ResultCode.GATEWAY_ERROR);
    }

    @ExceptionHandler(value = {ConnectTimeoutException.class})
    public CommonResult handle(ConnectTimeoutException ex) {
        log.error("connect timeout exception:{}", ex.getMessage());
        return CommonResult.failure(ResultCode.GATEWAY_CONNECT_TIME_OUT);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult handle(NotFoundException ex) {
        log.error("not found exception:{}", ex.getMessage());
        return CommonResult.failure(ResultCode.GATEWAY_NOT_FOUND_SERVICE);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult handle(RuntimeException ex) {
        log.error("runtime exception:{}", ex.getMessage());
        return CommonResult.failure(ResultCode.SYSTEM_INNER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult handle(Exception ex) {
        log.error("exception:{}", ex.getMessage());
        return CommonResult.failure(ResultCode.SYSTEM_INNER_ERROR);
    }

    @ExceptionHandler(value = {Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult handle(Throwable throwable) {
        CommonResult result = CommonResult.failure(ResultCode.SYSTEM_INNER_ERROR);
        if (throwable instanceof ResponseStatusException) {
            result = handle((ResponseStatusException) throwable);
        } else if (throwable instanceof ConnectTimeoutException) {
            result = handle((ConnectTimeoutException) throwable);
        } else if (throwable instanceof NotFoundException) {
            result = handle((NotFoundException) throwable);
        } else if (throwable instanceof RuntimeException) {
            result = handle((RuntimeException) throwable);
        } else if (throwable instanceof Exception) {
            result = handle((Exception) throwable);
        }
        return result;
    }
}
