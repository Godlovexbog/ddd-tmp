package net.zhaixing.blog.valuation.application.query.model.delivery;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交付记录 DTO
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Data
public class DeliveryDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

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
    private String deliveryStatus;

    /**
     * 交付时间
     */
    private LocalDateTime deliveryDate;
}
