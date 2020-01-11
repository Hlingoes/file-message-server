package cn.henry.study;

import cn.henry.study.result.ResultCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * description: 通用返回结果集测试
 *
 * @author Hlingoes
 * @date 2020/1/1 22:26
 */
@RunWith(SpringRunner.class)
public class CommonResultStandardTest {

    @Test
    public void testResultCode(){
        ResultCode[] ApiResultCodes = ResultCode.values();

    }

}
