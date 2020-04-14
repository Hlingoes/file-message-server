package cn.henry.study.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 表单注解
 *
 * @author Hlingoes 2019/12/22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FormAttribute {
    String label();

    String type() default "input";

    boolean required() default false;

    String alias() default "";

    String[] value() default "";

    boolean related() default false;
}
