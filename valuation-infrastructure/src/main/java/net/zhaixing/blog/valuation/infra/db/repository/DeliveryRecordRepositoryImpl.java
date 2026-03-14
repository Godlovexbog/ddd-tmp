package net.zhaixing.blog.valuation.infra.db.repository;

import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.CashDeliveryRecord;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.FinancialProductDeliveryRecord;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.repository.DeliveryRecordRepository;
import net.zhaixing.blog.valuation.domain.share.enums.TransactionStatusEnum;
import net.zhaixing.blog.valuation.infra.db.converter.DeliveryRecordConverter;
import net.zhaixing.blog.valuation.infra.db.mapper.DeliveryRecordMapper;
import net.zhaixing.blog.valuation.infra.db.model.CashDeliveryRecordPO;
import net.zhaixing.blog.valuation.infra.db.model.FinancialProductDeliveryRecordPO;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 交付记录仓储实现
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Repository
public class DeliveryRecordRepositoryImpl implements DeliveryRecordRepository {

    private final DeliveryRecordMapper deliveryRecordMapper;

    public DeliveryRecordRepositoryImpl(DeliveryRecordMapper deliveryRecordMapper) {
        this.deliveryRecordMapper = deliveryRecordMapper;
    }

    @Override
    public void delete(Long id) {
        CashDeliveryRecordPO po = new CashDeliveryRecordPO();
        po.setId(id);
        po.setIsDeleted(1);
        deliveryRecordMapper.updateById(po);
    }

    @Override
    public CashDeliveryRecord byId(Long id) {
        CashDeliveryRecordPO po = deliveryRecordMapper.selectById(id);
        if (Objects.isNull(po) || po.getIsDeleted() == 1) {
            return null;
        }
        return DeliveryRecordConverter.deserializeCash(po);
    }

    @Override
    public CashDeliveryRecord save(CashDeliveryRecord record) {
        CashDeliveryRecordPO po = DeliveryRecordConverter.serializeCash(record);
        if (Objects.isNull(record.getId())) {
            deliveryRecordMapper.insert(po);
        } else {
            deliveryRecordMapper.updateById(po);
        }
        return DeliveryRecordConverter.deserializeCash(deliveryRecordMapper.selectById(po.getId()));
    }

    @Override
    public List<CashDeliveryRecord> findCashByProjectId(Long projectId) {
        List<CashDeliveryRecordPO> pos = deliveryRecordMapper.selectCashByProjectId(projectId);
        return pos.stream()
                .filter(po -> po.getIsDeleted() != 1)
                .map(DeliveryRecordConverter::deserializeCash)
                .collect(Collectors.toList());
    }

    @Override
    public List<CashDeliveryRecord> listCashByDateAfterAndStatus(LocalDateTime afterDate, TransactionStatusEnum status) {
        List<CashDeliveryRecordPO> pos = deliveryRecordMapper.selectCashByDateAfterAndStatus(afterDate, status.getCode());
        return pos.stream()
                .filter(po -> po.getIsDeleted() != 1)
                .map(DeliveryRecordConverter::deserializeCash)
                .collect(Collectors.toList());
    }

    @Override
    public List<FinancialProductDeliveryRecord> listFinancialByDateAfterAndStatus(LocalDateTime afterDate, TransactionStatusEnum status) {
        List<net.zhaixing.blog.valuation.infra.db.model.FinancialProductDeliveryRecordPO> pos = 
                deliveryRecordMapper.selectFinancialByDateAfterAndStatus(afterDate, status.getCode());
        return pos.stream()
                .filter(po -> po.getIsDeleted() != 1)
                .map(DeliveryRecordConverter::deserializeFinancial)
                .collect(Collectors.toList());
    }

    @Override
    public FinancialProductDeliveryRecord saveFinancial(FinancialProductDeliveryRecord record) {
        FinancialProductDeliveryRecordPO po = DeliveryRecordConverter.serializeFinancial(record);
        if (Objects.isNull(record.getId())) {
            deliveryRecordMapper.insertFinancial(po);
        }
        return DeliveryRecordConverter.deserializeFinancial(po);
    }
}
