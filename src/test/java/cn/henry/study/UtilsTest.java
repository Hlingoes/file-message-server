package cn.henry.study;

import cn.henry.study.utils.FastDFSClientUtils;
import cn.henry.study.utils.FileHelpUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

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

    @Test
    public void testFdfsConfigReading() {
        System.out.println(FastDFSClientUtils.getClientConfigInfo());
    }
}
