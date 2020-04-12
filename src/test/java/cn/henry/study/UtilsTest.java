package cn.henry.study;

import cn.henry.study.entity.GeneratorQuartzJob;
import cn.henry.study.utils.FastDFSClientUtils;
import cn.henry.study.utils.FileHelpUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.InputStream;
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
    private static Logger logger = LoggerFactory.getLogger(UtilsTest.class);

    @Test
    public void testDeleteOutDateFiles() {
        FileHelpUtils.deleteOutDateFiles("F:\\", 7);
    }

    @Test
    public void testDeleteEmptyDir() {
        FileHelpUtils.deleteEmptyDir(new File("F:\\new"));
    }

    @Test
    public void testFdfsConfigReading() {
        System.out.println(FastDFSClientUtils.getClientConfigInfo());
    }

    @Test
    public void testMybatisGenerator() throws Exception {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        // 读取resource下的文件
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 获取单个文件
        Resource resource = resolver.getResource("generator/generatorConfig.xml");
        InputStream is = resource.getInputStream();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(is);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

    @Test
    public void testGeneratorToString() {
        logger.info("测试testGeneratorToString: {}", new GeneratorQuartzJob().toString());
    }
}
