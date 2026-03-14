package net.zhaixing.blog.valuation.infra.db.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.zhaixing.blog.user.common.model.result.BaseModel;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 估值记录持久化对象
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_valuation_record")
public class ValuationRecordPO extends BaseModel {

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目代码
     */
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
    private BigDecimal targetAmount;

    /**
     * 估值日期
     */
    private LocalDate valuationDate;

    /**
     * 标的份额
     */
    private BigDecimal targetShare;

    /**
     * 账户代码
     */
    private String accountCode;

    /**
     * 是否删除: 0-否, 1-是
     */
    @TableLogic(delval = "1", value = "0")
    private Integer isDeleted;
}
