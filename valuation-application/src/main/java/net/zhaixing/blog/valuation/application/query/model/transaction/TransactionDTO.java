package net.zhaixing.blog.valuation.application.query.model.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录 DTO
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Data
public class TransactionDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 标的ID
     */
    private Long targetId;

    /**
     * 标的代码
     */
    private String targetCode;

    /**
     * 标的来源
     */
    private String targetSource;

    /**
     * 交易份额
     */
    private BigDecimal transactionShare;

    /**
     * 交易金额
     */
    private BigDecimal transactionAmount;

    /**
     * 交易状态
     */
    private String transactionStatus;

    /**
     * 交易时间
     */
    private LocalDateTime transactionDate;
}
