package cn.henry.study.controller;

import cn.henry.study.base.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * description: 访问接口，通过service返回对应的结果集
 *
 * @author Hlingoes
 * @date 2019/12/21 18:19
 */
public class FileController {
    private  static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;


}
