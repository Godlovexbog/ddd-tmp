package net.zhaixing.blog.valuation.infra.db.converter;

import net.zhaixing.blog.valuation.domain.aggregate.transaction.model.TransactionRecord;
import net.zhaixing.blog.valuation.domain.share.enums.TransactionStatusEnum;
import net.zhaixing.blog.valuation.infra.db.model.TransactionRecordPO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 交易记录转换器
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public class TransactionRecordConverter {

    /**
     * PO 转领域模型
     */
    public static TransactionRecord deserialize(TransactionRecordPO po) {
        if (po == null) {
            return null;
        }
        return TransactionRecord.builder()
                .id(po.getId())
                .targetId(po.getTargetId())
                .targetCode(po.getTargetCode())
                .targetSource(po.getTargetSource())
                .transactionShare(po.getTransactionShare())
                .transactionAmount(po.getTransactionAmount())
                .transactionStatus(TransactionStatusEnum.fromCode(po.getTransactionStatus()))
                .transactionDate(po.getTransactionDate())
                .build();
    }

    /**
     * PO 列表转领域模型列表
     */
    public static List<TransactionRecord> deserializeList(List<TransactionRecordPO> pos) {
        if (pos == null) {
            return null;
        }
        return pos.stream()
                .map(TransactionRecordConverter::deserialize)
                .collect(Collectors.toList());
    }

    /**
     * 领域模型转 PO
     */
    public static TransactionRecordPO serialize(TransactionRecord record) {
        if (record == null) {
            return null;
        }
        TransactionRecordPO po = new TransactionRecordPO();
        po.setId(record.getId());
        po.setTargetId(record.getTargetId());
        po.setTargetCode(record.getTargetCode());
        po.setTargetSource(record.getTargetSource());
        po.setTransactionShare(record.getTransactionShare());
        po.setTransactionAmount(record.getTransactionAmount());
        if (record.getTransactionStatus() != null) {
            po.setTransactionStatus(record.getTransactionStatus().getCode());
        }
        po.setTransactionDate(record.getTransactionDate());
        return po;
    }
}
