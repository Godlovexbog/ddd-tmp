package net.zhaixing.blog.valuation.infra.db.converter;

import net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord;
import net.zhaixing.blog.valuation.infra.db.model.ValuationRecordPO;

/**
 * 估值记录转换器
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public class ValuationRecordConverter {

    /**
     * PO 转领域模型
     */
    public static ValuationRecord deserialize(ValuationRecordPO po) {
        if (po == null) {
            return null;
        }
        return ValuationRecord.builder()
                .id(po.getId())
                .projectId(po.getProjectId())
                .projectCode(po.getProjectCode())
                .targetSource(po.getTargetSource())
                .targetName(po.getTargetName())
                .targetAmount(po.getTargetAmount())
                .valuationDate(po.getValuationDate())
                .targetShare(po.getTargetShare())
                .accountCode(po.getAccountCode())
                .gmtCreate(po.getGmtCreate())
                .gmtModified(po.getGmtModified())
                .build();
    }

    /**
     * 领域模型转 PO
     */
    public static ValuationRecordPO serialize(ValuationRecord record) {
        if (record == null) {
            return null;
        }
        ValuationRecordPO po = new ValuationRecordPO();
        po.setId(record.getId());
        po.setProjectId(record.getProjectId());
        po.setProjectCode(record.getProjectCode());
        po.setTargetSource(record.getTargetSource());
        po.setTargetName(record.getTargetName());
        po.setTargetAmount(record.getTargetAmount());
        po.setValuationDate(record.getValuationDate());
        po.setTargetShare(record.getTargetShare());
        po.setAccountCode(record.getAccountCode());
        po.setGmtCreate(record.getGmtCreate());
        po.setGmtModified(record.getGmtModified());
        return po;
    }
}
