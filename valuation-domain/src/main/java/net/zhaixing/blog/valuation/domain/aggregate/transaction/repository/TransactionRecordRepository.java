package net.zhaixing.blog.valuation.domain.aggregate.transaction.repository;

import net.zhaixing.blog.user.common.domain.Repository;
import net.zhaixing.blog.valuation.domain.aggregate.transaction.model.TransactionRecord;
import net.zhaixing.blog.valuation.domain.share.enums.TransactionStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易记录仓储接口
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public interface TransactionRecordRepository extends Repository<TransactionRecord, Long> {

    /**
     * 根据标的ID查询交易记录
     *
     * @param targetId 标的ID
     * @return 交易记录列表
     */
    List<TransactionRecord> byTargetId(Long targetId);

    /**
     * 查询指定日期之后的成功交易记录
     *
     * @param afterDate 日期
     * @return 交易记录列表
     */
    List<TransactionRecord> listByDateAfter(LocalDateTime afterDate);

    /**
     * 查询指定日期之后的成功交易记录
     *
     * @param afterDate 日期
     * @param status   交易状态
     * @return 交易记录列表
     */
    List<TransactionRecord> listByDateAfterAndStatus(LocalDateTime afterDate, TransactionStatusEnum status);
}
