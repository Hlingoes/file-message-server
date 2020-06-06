package cn.henry.study.consumer.api;

import cn.henry.study.common.result.CommonResult;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/4/16 0:37
 */
@Component
public class JobApiHystrix implements JobApi {
    @Override
    public CommonResult addJob(String jobName) {
        return CommonResult.failure("web Server 的服务调用失败: " + jobName);
    }
}
