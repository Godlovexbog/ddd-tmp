package net.zhaixing.blog.valuation.infra.db.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.zhaixing.blog.user.common.model.result.BaseModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 金融产品交付记录持久化对象
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_financial_product_delivery_record")
public class FinancialProductDeliveryRecordPO extends BaseModel {

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
     * 交付状态: 0-PENDING, 1-SUCCESS, 2-FAILED
     */
    private String deliveryStatus;

    /**
     * 交付时间
     */
    private LocalDateTime deliveryDate;

    /**
     * 是否删除: 0-否, 1-是
     */
    @TableLogic(delval = "1", value = "0")
    private Integer isDeleted;
}
