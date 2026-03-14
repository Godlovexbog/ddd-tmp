package net.zhaixing.blog.valuation.application.command.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.zhaixing.blog.user.common.domain.Command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建交易记录命令
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Data
public class CreateTransactionCommand implements Command {

    /**
     * 标的ID
     */
    @NotNull(message = "{transaction.target.id.not.null}")
    private Long targetId;

    /**
     * 标的代码
     */
    @NotBlank(message = "{transaction.target.code.not.blank}")
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
    @NotNull(message = "{transaction.amount.not.null}")
    private BigDecimal transactionAmount;

    /**
     * 交易状态
     */
    @NotBlank(message = "{transaction.status.not.blank}")
    private String transactionStatus;

    /**
     * 交易时间
     */
    @NotNull(message = "{transaction.date.not.null}")
    private LocalDateTime transactionDate;
}
