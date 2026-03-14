package net.zhaixing.blog.valuation.domain.aggregate.valuation.service;

import net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord;

import java.math.BigDecimal;

/**
 * 估值计算服务接口
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public interface ValuationCalculationService {

    /**
     * 计算实时估值金额
     * 计算公式：估值记录金额 + 估值日期之后的成功交易记录金额 + 估值日期之后成功交付记录金额
     *
     * @param valuationRecord 估值记录
     * @return 实时估值金额
     */
    BigDecimal calculate(ValuationRecord valuationRecord);

    /**
     * 根据项目ID计算实时估值金额
     *
     * @param projectId 项目ID
     * @return 实时估值金额
     */
    BigDecimal calculateByProjectId(Long projectId);

    /**
     * 根据项目代码计算实时估值金额
     *
     * @param projectCode 项目代码
     * @return 实时估值金额
     */
    BigDecimal calculateByProjectCode(String projectCode);
}
