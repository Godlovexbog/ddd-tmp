package net.zhaixing.blog.valuation.domain.aggregate.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zhaixing.blog.user.common.domain.Entity;
import net.zhaixing.blog.valuation.domain.share.enums.TransactionStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录实体
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecord implements Entity {

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
    private TransactionStatusEnum transactionStatus;

    /**
     * 交易时间
     */
    private LocalDateTime transactionDate;
}
