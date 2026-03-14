package net.zhaixing.blog.valuation.domain.aggregate.valuation.repository;

import net.zhaixing.blog.user.common.domain.Repository;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord;

import java.util.List;

/**
 * 估值记录仓储接口
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public interface ValuationRecordRepository extends Repository<ValuationRecord, Long> {

    /**
     * 根据项目ID查询估值记录
     *
     * @param projectId 项目ID
     * @return 估值记录列表
     */
    List<ValuationRecord> byProjectId(Long projectId);

    /**
     * 根据项目代码查询估值记录
     *
     * @param projectCode 项目代码
     * @return 估值记录
     */
    ValuationRecord byProjectCode(String projectCode);
}
