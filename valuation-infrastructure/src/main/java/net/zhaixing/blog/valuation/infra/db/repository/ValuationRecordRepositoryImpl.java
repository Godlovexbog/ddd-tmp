package net.zhaixing.blog.valuation.infra.db.repository;

import net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.repository.ValuationRecordRepository;
import net.zhaixing.blog.valuation.infra.db.converter.ValuationRecordConverter;
import net.zhaixing.blog.valuation.infra.db.mapper.ValuationRecordMapper;
import net.zhaixing.blog.valuation.infra.db.model.ValuationRecordPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 估值记录仓储实现
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Repository
public class ValuationRecordRepositoryImpl implements ValuationRecordRepository {

    private final ValuationRecordMapper valuationRecordMapper;

    public ValuationRecordRepositoryImpl(ValuationRecordMapper valuationRecordMapper) {
        this.valuationRecordMapper = valuationRecordMapper;
    }

    @Override
    public void delete(Long id) {
        ValuationRecordPO po = new ValuationRecordPO();
        po.setId(id);
        po.setIsDeleted(1);
        valuationRecordMapper.updateById(po);
    }

    @Override
    public ValuationRecord byId(Long id) {
        ValuationRecordPO po = valuationRecordMapper.selectById(id);
        if (Objects.isNull(po) || po.getIsDeleted() == 1) {
            return null;
        }
        return ValuationRecordConverter.deserialize(po);
    }

    @Override
    public ValuationRecord save(ValuationRecord record) {
        ValuationRecordPO po = ValuationRecordConverter.serialize(record);
        if (Objects.isNull(record.getId())) {
            valuationRecordMapper.insert(po);
        } else {
            valuationRecordMapper.updateById(po);
        }
        return ValuationRecordConverter.deserialize(valuationRecordMapper.selectById(po.getId()));
    }

    @Override
    public List<ValuationRecord> byProjectId(Long projectId) {
        List<ValuationRecordPO> pos = valuationRecordMapper.selectByProjectId(projectId);
        return pos.stream()
                .map(ValuationRecordConverter::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public ValuationRecord byProjectCode(String projectCode) {
        ValuationRecordPO po = valuationRecordMapper.selectByProjectCode(projectCode);
        if (Objects.isNull(po) || po.getIsDeleted() == 1) {
            return null;
        }
        return ValuationRecordConverter.deserialize(po);
    }
}
