package cn.henry.study.consumer.controller;

import cn.henry.study.consumer.api.JobApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/4/16 0:44
 */
@RestController
public class ConsumerController {
    @Autowired
    private JobApi jobApi;

    @GetMapping("/testAddJob")
    public String get(String jobName) {
        return "Consumer: " + jobApi.addJob(jobName);
    }
}
