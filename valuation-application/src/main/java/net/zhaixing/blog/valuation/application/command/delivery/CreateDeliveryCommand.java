package net.zhaixing.blog.valuation.application.command.delivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.zhaixing.blog.user.common.domain.Command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建交付记录命令
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Data
public class CreateDeliveryCommand implements Command {

    /**
     * 项目ID
     */
    @NotNull(message = "{delivery.project.id.not.null}")
    private Long projectId;

    /**
     * 交付类型: CASH-现金交付, FINANCIAL-金融产品
     */
    @NotBlank(message = "{delivery.type.not.blank}")
    private String deliveryType;

    /**
     * 产品代码（金融产品）
     */
    private String productCode;

    /**
     * 产品名称（金融产品）
     */
    private String productName;

    /**
     * 现金金额（现金交付）
     */
    private BigDecimal cashAmount;

    /**
     * 交付金额（金融产品）
     */
    private BigDecimal deliveryAmount;

    /**
     * 交付状态
     */
    @NotBlank(message = "{delivery.status.not.blank}")
    private String deliveryStatus;

    /**
     * 交付时间
     */
    @NotNull(message = "{delivery.date.not.null}")
    private LocalDateTime deliveryDate;
}
