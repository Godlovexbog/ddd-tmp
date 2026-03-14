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
 * 现金交付记录实体
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
public class CashDeliveryRecord implements Entity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 现金金额
     */
    private BigDecimal cashAmount;

    /**
     * 交付状态
     */
    private TransactionStatusEnum deliveryStatus;

    /**
     * 交付时间
     */
    private LocalDateTime deliveryDate;
}
