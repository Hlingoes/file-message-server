package cn.henry.study.consumer.api;

import cn.henry.study.common.result.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/4/16 0:37
 */
@Component
@FeignClient(value = "web-server", fallback = JobApiHystrix.class)
public interface JobApi {
    /**
     * description: 测试添加任务
     *
     * @param jobName
     * @return java.lang.String
     * @author Hlingoes 2020/4/16
     */
    @GetMapping("/job/testAdding")
    CommonResult addJob(@RequestParam("jobName") String jobName);
}
