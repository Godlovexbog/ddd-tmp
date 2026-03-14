package net.zhaixing.blog.valuation.application.query.impl;

import net.zhaixing.blog.user.common.model.query.KeywordQuery;
import net.zhaixing.blog.user.common.model.result.Page;
import net.zhaixing.blog.valuation.application.query.model.valuation.ValuationDTO;
import net.zhaixing.blog.valuation.application.query.valuation.ValuationQueryService;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.repository.ValuationRecordRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 估值记录查询服务实现
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Service
public class ValuationQueryServiceImpl implements ValuationQueryService {

    private final ValuationRecordRepository valuationRecordRepository;

    public ValuationQueryServiceImpl(ValuationRecordRepository valuationRecordRepository) {
        this.valuationRecordRepository = valuationRecordRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ValuationDTO getById(Long id) {
        ValuationRecord record = valuationRecordRepository.byId(id);
        return convertToDTO(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValuationDTO> getByProjectId(Long projectId) {
        List<ValuationRecord> records = valuationRecordRepository.byProjectId(projectId);
        return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ValuationDTO getByProjectCode(String projectCode) {
        ValuationRecord record = valuationRecordRepository.byProjectCode(projectCode);
        return convertToDTO(record);
    }

    private ValuationDTO convertToDTO(ValuationRecord record) {
        if (record == null) {
            return null;
        }
        ValuationDTO dto = new ValuationDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }
}
