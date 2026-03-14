package net.zhaixing.blog.valuation.infra.db.converter;

import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.CashDeliveryRecord;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.FinancialProductDeliveryRecord;
import net.zhaixing.blog.valuation.domain.share.enums.TransactionStatusEnum;
import net.zhaixing.blog.valuation.infra.db.model.CashDeliveryRecordPO;
import net.zhaixing.blog.valuation.infra.db.model.FinancialProductDeliveryRecordPO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 交付记录转换器
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public class DeliveryRecordConverter {

    /**
     * 现金交付 PO 转领域模型
     */
    public static CashDeliveryRecord deserializeCash(CashDeliveryRecordPO po) {
        if (po == null) {
            return null;
        }
        return CashDeliveryRecord.builder()
                .id(po.getId())
                .projectId(po.getProjectId())
                .cashAmount(po.getCashAmount())
                .deliveryStatus(TransactionStatusEnum.fromCode(po.getDeliveryStatus()))
                .deliveryDate(po.getDeliveryDate())
                .build();
    }

    /**
     * 现金交付 PO 列表转领域模型列表
     */
    public static List<CashDeliveryRecord> deserializeCashList(List<CashDeliveryRecordPO> pos) {
        if (pos == null) {
            return null;
        }
        return pos.stream()
                .map(DeliveryRecordConverter::deserializeCash)
                .collect(Collectors.toList());
    }

    /**
     * 金融产品交付 PO 转领域模型
     */
    public static FinancialProductDeliveryRecord deserializeFinancial(FinancialProductDeliveryRecordPO po) {
        if (po == null) {
            return null;
        }
        return FinancialProductDeliveryRecord.builder()
                .id(po.getId())
                .projectId(po.getProjectId())
                .productCode(po.getProductCode())
                .productName(po.getProductName())
                .deliveryAmount(po.getDeliveryAmount())
                .deliveryStatus(TransactionStatusEnum.fromCode(po.getDeliveryStatus()))
                .deliveryDate(po.getDeliveryDate())
                .build();
    }

    /**
     * 金融产品交付 PO 列表转领域模型列表
     */
    public static List<FinancialProductDeliveryRecord> deserializeFinancialList(List<FinancialProductDeliveryRecordPO> pos) {
        if (pos == null) {
            return null;
        }
        return pos.stream()
                .map(DeliveryRecordConverter::deserializeFinancial)
                .collect(Collectors.toList());
    }

    /**
     * 现金交付领域模型转 PO
     */
    public static CashDeliveryRecordPO serializeCash(CashDeliveryRecord record) {
        if (record == null) {
            return null;
        }
        CashDeliveryRecordPO po = new CashDeliveryRecordPO();
        po.setId(record.getId());
        po.setProjectId(record.getProjectId());
        po.setCashAmount(record.getCashAmount());
        if (record.getDeliveryStatus() != null) {
            po.setDeliveryStatus(record.getDeliveryStatus().getCode());
        }
        po.setDeliveryDate(record.getDeliveryDate());
        return po;
    }

    /**
     * 金融产品交付领域模型转 PO
     */
    public static FinancialProductDeliveryRecordPO serializeFinancial(FinancialProductDeliveryRecord record) {
        if (record == null) {
            return null;
        }
        FinancialProductDeliveryRecordPO po = new FinancialProductDeliveryRecordPO();
        po.setId(record.getId());
        po.setProjectId(record.getProjectId());
        po.setProductCode(record.getProductCode());
        po.setProductName(record.getProductName());
        po.setDeliveryAmount(record.getDeliveryAmount());
        if (record.getDeliveryStatus() != null) {
            po.setDeliveryStatus(record.getDeliveryStatus().getCode());
        }
        po.setDeliveryDate(record.getDeliveryDate());
        return po;
    }
}
