package net.zhaixing.blog.valuation.infra.db.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.zhaixing.blog.user.common.model.result.BaseModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 现金交付记录持久化对象
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_cash_delivery_record")
public class CashDeliveryRecordPO extends BaseModel {

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 现金金额
     */
    private BigDecimal cashAmount;

    /**
     * 交付状态: 0-PENDING, 1-SUCCESS, 2-FAILED
     */
    private String deliveryStatus;

    /**
     * 交付时间
     */
    private LocalDateTime deliveryDate;
}
