package net.zhaixing.blog.valuation.domain.share.enums;

/**
 * 交易状态枚举
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public enum TransactionStatusEnum {
    /**
     * 处理中
     */
    PENDING("0", "处理中"),
    /**
     * 成功
     */
    SUCCESS("1", "成功"),
    /**
     * 失败
     */
    FAILED("2", "失败");

    private final String code;
    private final String desc;

    TransactionStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TransactionStatusEnum fromCode(String code) {
        for (TransactionStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
