package net.zhaixing.blog.user.common.annotation;

import java.lang.annotation.*;

/**
 * 敏感数据脱敏注解
 * 作用于字段，在序列化时自动脱敏
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sensitive {

    /**
     * 脱敏类型
     */
    SensitiveType value() default SensitiveType.PHONE;
}
