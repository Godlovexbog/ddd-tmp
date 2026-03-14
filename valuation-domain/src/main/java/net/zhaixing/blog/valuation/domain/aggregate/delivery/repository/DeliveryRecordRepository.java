package net.zhaixing.blog.valuation.domain.aggregate.delivery.repository;

import net.zhaixing.blog.user.common.domain.Repository;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.CashDeliveryRecord;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.FinancialProductDeliveryRecord;
import net.zhaixing.blog.valuation.domain.share.enums.TransactionStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交付记录仓储接口
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public interface DeliveryRecordRepository extends Repository<CashDeliveryRecord, Long> {

    /**
     * 根据项目ID查询现金交付记录
     *
     * @param projectId 项目ID
     * @return 现金交付记录列表
     */
    List<CashDeliveryRecord> findCashByProjectId(Long projectId);

    /**
     * 查询指定日期之后的成功现金交付记录
     *
     * @param afterDate 日期
     * @param status   交付状态
     * @return 现金交付记录列表
     */
    List<CashDeliveryRecord> listCashByDateAfterAndStatus(LocalDateTime afterDate, TransactionStatusEnum status);

    /**
     * 查询指定日期之后的成功金融产品交付记录
     *
     * @param afterDate 日期
     * @param status   交付状态
     * @return 金融产品交付记录列表
     */
    List<FinancialProductDeliveryRecord> listFinancialByDateAfterAndStatus(LocalDateTime afterDate, TransactionStatusEnum status);

    /**
     * 保存金融产品交付记录
     *
     * @param record 金融产品交付记录
     * @return 保存后的记录
     */
    FinancialProductDeliveryRecord saveFinancial(FinancialProductDeliveryRecord record);
}
