package net.zhaixing.blog.user.common.annotation;

/**
 * 敏感数据脱敏注解
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public enum SensitiveType {

    /**
     * 手机号脱敏: 138****1234
     */
    PHONE,

    /**
     * 邮箱脱敏: a***@example.com
     */
    EMAIL,

    /**
     * 身份证脱敏: 310***********1234
     */
    ID_CARD,

    /**
     * 银行卡脱敏: 6222 **** **** 1234
     */
    BANK_CARD,

    /**
     * 姓名脱敏: 张*
     */
    NAME
}
