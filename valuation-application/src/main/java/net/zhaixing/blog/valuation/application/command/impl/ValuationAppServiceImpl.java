package net.zhaixing.blog.valuation.application.command.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.zhaixing.blog.valuation.application.command.ValuationAppService;
import net.zhaixing.blog.valuation.application.command.delivery.CreateDeliveryCommand;
import net.zhaixing.blog.valuation.application.command.transaction.CreateTransactionCommand;
import net.zhaixing.blog.valuation.application.command.valuation.CreateValuationCommand;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.CashDeliveryRecord;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.FinancialProductDeliveryRecord;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.repository.DeliveryRecordRepository;
import net.zhaixing.blog.valuation.domain.aggregate.transaction.model.TransactionRecord;
import net.zhaixing.blog.valuation.domain.aggregate.transaction.repository.TransactionRecordRepository;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.repository.ValuationRecordRepository;
import net.zhaixing.blog.valuation.domain.share.enums.TransactionStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 估值应用服务实现
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Slf4j
@Service
public class ValuationAppServiceImpl implements ValuationAppService {

    @Resource
    private ValuationRecordRepository valuationRecordRepository;

    @Resource
    private TransactionRecordRepository transactionRecordRepository;

    @Resource
    private DeliveryRecordRepository deliveryRecordRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createValuation(CreateValuationCommand command) {
        ValuationRecord record = ValuationRecord.builder()
                .projectId(command.getProjectId())
                .projectCode(command.getProjectCode())
                .targetSource(command.getTargetSource())
                .targetName(command.getTargetName())
                .targetAmount(command.getTargetAmount())
                .valuationDate(command.getValuationDate())
                .targetShare(command.getTargetShare())
                .accountCode(command.getAccountCode())
                .build();
        valuationRecordRepository.save(record);
        log.info("创建估值记录成功: {}", record.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTransaction(CreateTransactionCommand command) {
        TransactionRecord record = TransactionRecord.builder()
                .targetId(command.getTargetId())
                .targetCode(command.getTargetCode())
                .targetSource(command.getTargetSource())
                .transactionShare(command.getTransactionShare())
                .transactionAmount(command.getTransactionAmount())
                .transactionStatus(TransactionStatusEnum.fromCode(command.getTransactionStatus()))
                .transactionDate(command.getTransactionDate())
                .build();
        transactionRecordRepository.save(record);
        log.info("创建交易记录成功: {}", record.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDelivery(CreateDeliveryCommand command) {
        if ("CASH".equals(command.getDeliveryType())) {
            CashDeliveryRecord record = CashDeliveryRecord.builder()
                    .projectId(command.getProjectId())
                    .cashAmount(command.getCashAmount())
                    .deliveryStatus(TransactionStatusEnum.fromCode(command.getDeliveryStatus()))
                    .deliveryDate(command.getDeliveryDate())
                    .build();
            deliveryRecordRepository.save(record);
            log.info("创建现金交付记录成功: {}", record.getId());
        } else {
            FinancialProductDeliveryRecord record = FinancialProductDeliveryRecord.builder()
                    .projectId(command.getProjectId())
                    .productCode(command.getProductCode())
                    .productName(command.getProductName())
                    .deliveryAmount(command.getDeliveryAmount())
                    .deliveryStatus(TransactionStatusEnum.fromCode(command.getDeliveryStatus()))
                    .deliveryDate(command.getDeliveryDate())
                    .build();
            deliveryRecordRepository.saveFinancial(record);
            log.info("创建金融产品交付记录成功: {}", record.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteValuation(Long id) {
        valuationRecordRepository.delete(id);
        log.info("删除估值记录成功: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTransaction(Long id) {
        transactionRecordRepository.delete(id);
        log.info("删除交易记录成功: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDelivery(Long id) {
        deliveryRecordRepository.delete(id);
        log.info("删除交付记录成功: {}", id);
    }
}
