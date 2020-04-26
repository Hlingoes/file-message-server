package cn.henry.study.web.service.files;

import cn.henry.study.web.anno.FormAttribute;
import cn.henry.study.common.base.FormField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * description: 默认使用RestTemplate处理http下载服务
 *
 * @author Hlingoes
 * @date 2019/12/21 23:59
 */
@Service
public class DefaultFileService implements FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFileService.class);

    @Override
    public boolean download(String remotePath, String localPath) {
        return false;
    }

    @Override
    public boolean upload(String remotePath, File file) {
        return false;
    }

    @Override
    public boolean upload(String remotePath, InputStream inputStream) {
        return false;
    }

    @Override
    public boolean delete(String remotePath) {
        return false;
    }

    @Override
    public List<String> filter(String filePath, Pattern pattern) {
        return null;
    }

    @Override
    public Class<?> getEntityClazz() {
        return null;
    }

    /**
     * description: 反射成员变量的类型，取值，赋值
     * 示例获取java 反射获取字段为List类型中的泛型类型
     *
     * @param clazz
     * @return java.util.List<FormField>
     * @author Hlingoes 2019/12/22
     */
    public List<FormField> getFormFields(Class<?> clazz) {
        List<FormField> formFields = new ArrayList<>();
        ReflectionUtils.doWithFields(clazz, field -> {
            ReflectionUtils.makeAccessible(field);
            if (field.isAnnotationPresent(FormAttribute.class)) {
                FormAttribute formAttribute = field.getAnnotation(FormAttribute.class);
                FormField formField = new FormField();
                formField.setProp(field.getName());
                formField.setLabel(formAttribute.label());
                formField.setType(formAttribute.type());
                if (formAttribute.related()) {
                    // 设置字段可访问（必须，否则报错）
                    field.setAccessible(true);
                    Class<?> curFieldType = field.getType();
                    // 集合List元素
                    LOGGER.info("成员变量的数据类型: {}", curFieldType);
                    if (curFieldType.equals(List.class)) {
                        // 当前集合的泛型类型
                        Type genericType = field.getGenericType();
                        if (null != genericType && genericType instanceof ParameterizedType) {
                            ParameterizedType pt = (ParameterizedType) genericType;
                            // 得到泛型里的class类型对象
                            Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                            //  需要按业务实现
                            formField.setValue(Arrays.asList("hulin", "henry", "Hingoes"));
                            LOGGER.info("泛型里的class类型: {}", actualTypeArgument);
                        }
                    }
                }
                formFields.add(formField);
            }
        });
        return formFields;
    }
}
