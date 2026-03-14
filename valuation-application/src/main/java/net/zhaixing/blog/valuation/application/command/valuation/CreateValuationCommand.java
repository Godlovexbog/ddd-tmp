package net.zhaixing.blog.valuation.application.command.valuation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import net.zhaixing.blog.user.common.domain.Command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建估值记录命令
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Data
public class CreateValuationCommand implements Command {

    /**
     * 项目ID
     */
    @NotNull(message = "{valuation.project.id.not.null}")
    private Long projectId;

    /**
     * 项目代码
     */
    @NotBlank(message = "{valuation.project.code.not.blank}")
    private String projectCode;

    /**
     * 标的来源
     */
    private String targetSource;

    /**
     * 标的名称
     */
    private String targetName;

    /**
     * 标的金额
     */
    @NotNull(message = "{valuation.target.amount.not.null}")
    private BigDecimal targetAmount;

    /**
     * 估值日期
     */
    @NotNull(message = "{valuation.date.not.null}")
    private LocalDate valuationDate;

    /**
     * 标的份额
     */
    private BigDecimal targetShare;

    /**
     * 账户代码
     */
    private String accountCode;
}
