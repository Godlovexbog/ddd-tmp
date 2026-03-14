package net.zhaixing.blog.valuation.domain.aggregate.delivery.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zhaixing.blog.user.common.domain.Entity;
import net.zhaixing.blog.valuation.domain.share.enums.TransactionStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 金融产品交付记录实体
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
public class FinancialProductDeliveryRecord implements Entity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 产品代码
     */
    private String productCode;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 交付金额
     */
    private BigDecimal deliveryAmount;

    /**
     * 交付状态
     */
    private TransactionStatusEnum deliveryStatus;

    /**
     * 交付时间
     */
    private LocalDateTime deliveryDate;
}
