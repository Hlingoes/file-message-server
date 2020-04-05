package cn.henry.study;

import cn.henry.study.utils.FastDFSClientUtils;
import cn.henry.study.utils.FileHelpUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * description: 测试工具类
 *
 * @author Hlingoes
 * @date 2020/1/15 23:17
 */

@RunWith(SpringRunner.class)
public class UtilsTest {

    public void testDeleteOutDateFiles() {
        FileHelpUtils.deleteOutDateFiles("F:\\", 7);
    }

    @Test
    public void testDeleteEmptyDir() {
        FileHelpUtils.deleteEmptyDir(new File("F:\\new"));
    }


    public void testFdfsConfigReading() {
        System.out.println(FastDFSClientUtils.getClientConfigInfo());
    }

    @Test
    public void testMybatisGenerator() throws Exception {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File("generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }
}
