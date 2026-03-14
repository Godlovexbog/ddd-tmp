package net.zhaixing.blog.valuation.application.query.model.valuation;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 估值记录 DTO
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Data
public class ValuationDTO {

    /**
     * 主键ID
     */
    private Long id;

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
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;
}
