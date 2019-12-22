package cn.henry.study.utils;

import cn.henry.study.configuration.HttpClientConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2019/12/21 22:04
 */
public class JacksonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientConfig.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * description: 将对象转换成字符串
     *
     * @param data
     * @return java.lang.String
     * @author Hlingoes 2019/12/21
     */
    public static String object2Str(Object data) {
        try {
            String string = MAPPER.writeValueAsString(data);
            return string;
        } catch (JsonProcessingException e) {
            LOGGER.error("对象转string失败: {}", data, e);
        }
        return null;
    }

    /**
     * description: 将字符串转化为对象
     *
     * @param data
     * @param beanType
     * @return T
     * @author Hlingoes 2019/12/21
     */
    public static <T> T str2Bean(String data, Class<T> beanType) {
        try {
            T t = MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(data, beanType);
            return t;
        } catch (Exception e) {
            LOGGER.error("string转对象失败: {}", data, e);
        }
        return null;
    }

    /**
     * description: 将字符串转换成pojo对象list
     *
     * @param data
     * @param beanType
     * @return java.util.List<T>
     * @author Hlingoes 2019/12/21
     */
    public static <T> List<T> str2List(String data, Class<T> beanType) {
        JavaType javaType = MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = MAPPER.readValue(data, javaType);
            return list;
        } catch (Exception e) {
            LOGGER.error("string转对象失败: {}", data, e);
        }
        return null;
    }
}
