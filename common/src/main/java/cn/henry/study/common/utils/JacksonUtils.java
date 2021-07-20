package cn.henry.study.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
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
    private static Logger logger = LoggerFactory.getLogger(JacksonUtils.class);

    public static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * description: 将对象转换成字符串
     *
     * @param data
     * @return java.lang.String
     * @author Hlingoes 2019/12/21
     */
    public static String object2Str(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.warn("object2Str fail", e);
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
            return MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(data, beanType);
        } catch (JsonProcessingException e) {
            logger.warn("str2Bean fail", e);
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
            return MAPPER.readValue(data, javaType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.warn("str2List fail", e);
        }
        return null;
    }

}
