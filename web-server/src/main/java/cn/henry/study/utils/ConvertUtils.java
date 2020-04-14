package cn.henry.study.utils;

import cn.henry.study.result.ParameterInvalidItem;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;

/**
 * description: 参数无效项辅助器
 *
 * @author Hlingoes
 * @date 2020/1/1 23:33
 */
public class ConvertUtils {

    /**
     * description: 将无效参数转为实体辅助类
     *
     * @param cvset
     * @return java.util.List<cn.henry.study.result.ParameterInvalidItem>
     * @author Hlingoes 2020/1/1
     */
    public static List<ParameterInvalidItem> convertCVSetToParameterInvalidItemList(Set<ConstraintViolation<?>> cvset) {
        if (CollectionUtils.isEmpty(cvset)) {
            return null;
        }

        List<ParameterInvalidItem> parameterInvalidItemList = Lists.newArrayList();
        for (ConstraintViolation<?> cv : cvset) {
            ParameterInvalidItem parameterInvalidItem = new ParameterInvalidItem();
            String propertyPath = cv.getPropertyPath().toString();
            if (propertyPath.indexOf(".") != -1) {
                String[] propertyPathArr = propertyPath.split("\\.");
                parameterInvalidItem.setFieldName(propertyPathArr[propertyPathArr.length - 1]);
            } else {
                parameterInvalidItem.setFieldName(propertyPath);
            }
            parameterInvalidItem.setMessage(cv.getMessage());
            parameterInvalidItemList.add(parameterInvalidItem);
        }

        return parameterInvalidItemList;
    }

    /**
     * description: 将无效绑定参数转为实体辅助类
     *
     * @param bindingResult
     * @return java.util.List<cn.henry.study.result.ParameterInvalidItem>
     * @author Hlingoes 2020/1/1
     */
    public static List<ParameterInvalidItem> convertBindingResultToMapParameterInvalidItemList(BindingResult bindingResult) {
        if (bindingResult == null) {
            return null;
        }
        List<ParameterInvalidItem> parameterInvalidItemList = Lists.newArrayList();
        List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrorList) {
            ParameterInvalidItem parameterInvalidItem = new ParameterInvalidItem();
            parameterInvalidItem.setFieldName(fieldError.getField());
            parameterInvalidItem.setMessage(fieldError.getDefaultMessage());
            parameterInvalidItemList.add(parameterInvalidItem);
        }
        return parameterInvalidItemList;
    }
}
