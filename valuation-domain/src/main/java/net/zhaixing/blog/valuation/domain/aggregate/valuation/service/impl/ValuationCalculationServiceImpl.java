package net.zhaixing.blog.valuation.domain.aggregate.valuation.service.impl;

import net.zhaixing.blog.user.common.domain.DomainService;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.CashDeliveryRecord;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.FinancialProductDeliveryRecord;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.repository.DeliveryRecordRepository;
import net.zhaixing.blog.valuation.domain.aggregate.transaction.model.TransactionRecord;
import net.zhaixing.blog.valuation.domain.aggregate.transaction.repository.TransactionRecordRepository;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.repository.ValuationRecordRepository;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.service.ValuationCalculationService;
import net.zhaixing.blog.valuation.domain.share.enums.TransactionStatusEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 估值计算服务实现
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Service
public class ValuationCalculationServiceImpl implements ValuationCalculationService {

    private final ValuationRecordRepository valuationRecordRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final DeliveryRecordRepository deliveryRecordRepository;

    public ValuationCalculationServiceImpl(
            ValuationRecordRepository valuationRecordRepository,
            TransactionRecordRepository transactionRecordRepository,
            DeliveryRecordRepository deliveryRecordRepository) {
        this.valuationRecordRepository = valuationRecordRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.deliveryRecordRepository = deliveryRecordRepository;
    }

    @Override
    public BigDecimal calculate(ValuationRecord valuationRecord) {
        if (valuationRecord == null) {
            return BigDecimal.ZERO;
        }

        // 1. 获取估值记录金额
        BigDecimal valuationAmount = valuationRecord.getTargetAmount() != null 
                ? valuationRecord.getTargetAmount() 
                : BigDecimal.ZERO;

        // 2. 获取估值日期之后的成功交易记录金额
        LocalDateTime valuationDateTime = valuationRecord.getValuationDate().atStartOfDay();
        List<TransactionRecord> successTransactions = transactionRecordRepository
                .listByDateAfterAndStatus(valuationDateTime, TransactionStatusEnum.SUCCESS);
        BigDecimal transactionAmount = successTransactions.stream()
                .filter(t -> t.getTransactionAmount() != null)
                .map(TransactionRecord::getTransactionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 获取估值日期之后的成功现金交付记录金额
        List<CashDeliveryRecord> successCashDeliveries = deliveryRecordRepository
                .listCashByDateAfterAndStatus(valuationDateTime, TransactionStatusEnum.SUCCESS);
        BigDecimal cashDeliveryAmount = successCashDeliveries.stream()
                .filter(d -> d.getCashAmount() != null)
                .map(CashDeliveryRecord::getCashAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. 获取估值日期之后的成功金融产品交付记录金额
        List<FinancialProductDeliveryRecord> successFinancialDeliveries = deliveryRecordRepository
                .listFinancialByDateAfterAndStatus(valuationDateTime, TransactionStatusEnum.SUCCESS);
        BigDecimal financialDeliveryAmount = successFinancialDeliveries.stream()
                .filter(d -> d.getDeliveryAmount() != null)
                .map(FinancialProductDeliveryRecord::getDeliveryAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. 计算总金额
        return valuationAmount
                .add(transactionAmount)
                .add(cashDeliveryAmount)
                .add(financialDeliveryAmount);
    }

    @Override
    public BigDecimal calculateByProjectId(Long projectId) {
        ValuationRecord valuationRecord = valuationRecordRepository.byProjectId(projectId).stream()
                .findFirst()
                .orElse(null);
        return calculate(valuationRecord);
    }

    @Override
    public BigDecimal calculateByProjectCode(String projectCode) {
        ValuationRecord valuationRecord = valuationRecordRepository.byProjectCode(projectCode);
        return calculate(valuationRecord);
    }
}
