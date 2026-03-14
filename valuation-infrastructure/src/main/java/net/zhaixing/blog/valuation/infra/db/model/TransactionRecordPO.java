package net.zhaixing.blog.valuation.infra.db.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.zhaixing.blog.user.common.model.result.BaseModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录持久化对象
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_transaction_record")
public class TransactionRecordPO extends BaseModel {

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
     * 交易状态: 0-PENDING, 1-SUCCESS, 2-FAILED
     */
    private String transactionStatus;

    /**
     * 交易时间
     */
    private LocalDateTime transactionDate;
}
