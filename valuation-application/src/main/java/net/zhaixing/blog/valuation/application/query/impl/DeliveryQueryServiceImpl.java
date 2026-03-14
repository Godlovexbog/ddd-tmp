package net.zhaixing.blog.valuation.application.query.impl;

import net.zhaixing.blog.valuation.application.query.delivery.DeliveryQueryService;
import net.zhaixing.blog.valuation.application.query.model.delivery.DeliveryDTO;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.CashDeliveryRecord;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.repository.DeliveryRecordRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 交付记录查询服务实现
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Service
public class DeliveryQueryServiceImpl implements DeliveryQueryService {

    private final DeliveryRecordRepository deliveryRecordRepository;

    public DeliveryQueryServiceImpl(DeliveryRecordRepository deliveryRecordRepository) {
        this.deliveryRecordRepository = deliveryRecordRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDTO getById(Long id) {
        CashDeliveryRecord record = deliveryRecordRepository.byId(id);
        return convertToDTO(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDTO> getCashByProjectId(Long projectId) {
        List<CashDeliveryRecord> records = deliveryRecordRepository.findCashByProjectId(projectId);
        return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private DeliveryDTO convertToDTO(CashDeliveryRecord record) {
        if (record == null) {
            return null;
        }
        DeliveryDTO dto = new DeliveryDTO();
        dto.setId(record.getId());
        dto.setProjectId(record.getProjectId());
        dto.setCashAmount(record.getCashAmount());
        if (record.getDeliveryStatus() != null) {
            dto.setDeliveryStatus(record.getDeliveryStatus().getCode());
        }
        dto.setDeliveryDate(record.getDeliveryDate());
        return dto;
    }
}
