package net.zhaixing.blog.valuation.infra.db.repository;

import net.zhaixing.blog.valuation.domain.aggregate.transaction.model.TransactionRecord;
import net.zhaixing.blog.valuation.domain.aggregate.transaction.repository.TransactionRecordRepository;
import net.zhaixing.blog.valuation.domain.share.enums.TransactionStatusEnum;
import net.zhaixing.blog.valuation.infra.db.converter.TransactionRecordConverter;
import net.zhaixing.blog.valuation.infra.db.mapper.TransactionRecordMapper;
import net.zhaixing.blog.valuation.infra.db.model.TransactionRecordPO;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 交易记录仓储实现
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Repository
public class TransactionRecordRepositoryImpl implements TransactionRecordRepository {

    private final TransactionRecordMapper transactionRecordMapper;

    public TransactionRecordRepositoryImpl(TransactionRecordMapper transactionRecordMapper) {
        this.transactionRecordMapper = transactionRecordMapper;
    }

    @Override
    public void delete(Long id) {
        TransactionRecordPO po = new TransactionRecordPO();
        po.setId(id);
        po.setIsDeleted(1);
        transactionRecordMapper.updateById(po);
    }

    @Override
    public TransactionRecord byId(Long id) {
        TransactionRecordPO po = transactionRecordMapper.selectById(id);
        if (Objects.isNull(po) || po.getIsDeleted() == 1) {
            return null;
        }
        return TransactionRecordConverter.deserialize(po);
    }

    @Override
    public TransactionRecord save(TransactionRecord record) {
        TransactionRecordPO po = TransactionRecordConverter.serialize(record);
        if (Objects.isNull(record.getId())) {
            transactionRecordMapper.insert(po);
        } else {
            transactionRecordMapper.updateById(po);
        }
        return TransactionRecordConverter.deserialize(transactionRecordMapper.selectById(po.getId()));
    }

    @Override
    public List<TransactionRecord> byTargetId(Long targetId) {
        List<TransactionRecordPO> pos = transactionRecordMapper.selectByTargetId(targetId);
        return pos.stream()
                .filter(po -> po.getIsDeleted() != 1)
                .map(TransactionRecordConverter::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionRecord> listByDateAfter(LocalDateTime afterDate) {
        List<TransactionRecordPO> pos = transactionRecordMapper.selectByDateAfterAndStatus(afterDate, TransactionStatusEnum.SUCCESS.getCode());
        return pos.stream()
                .filter(po -> po.getIsDeleted() != 1)
                .map(TransactionRecordConverter::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionRecord> listByDateAfterAndStatus(LocalDateTime afterDate, TransactionStatusEnum status) {
        List<TransactionRecordPO> pos = transactionRecordMapper.selectByDateAfterAndStatus(afterDate, status.getCode());
        return pos.stream()
                .filter(po -> po.getIsDeleted() != 1)
                .map(TransactionRecordConverter::deserialize)
                .collect(Collectors.toList());
    }
}
