package cn.henry.study.api;

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
    public String addJob(String jobName) {
        return "web Server 的服务调用失败: " + jobName;
    }
}
