package net.zhaixing.blog.valuation.application.query.impl;

import net.zhaixing.blog.valuation.application.query.model.transaction.TransactionDTO;
import net.zhaixing.blog.valuation.application.query.transaction.TransactionQueryService;
import net.zhaixing.blog.valuation.domain.aggregate.transaction.model.TransactionRecord;
import net.zhaixing.blog.valuation.domain.aggregate.transaction.repository.TransactionRecordRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 交易记录查询服务实现
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Service
public class TransactionQueryServiceImpl implements TransactionQueryService {

    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionQueryServiceImpl(TransactionRecordRepository transactionRecordRepository) {
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDTO getById(Long id) {
        TransactionRecord record = transactionRecordRepository.byId(id);
        return convertToDTO(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getByTargetId(Long targetId) {
        List<TransactionRecord> records = transactionRecordRepository.byTargetId(targetId);
        return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TransactionDTO convertToDTO(TransactionRecord record) {
        if (record == null) {
            return null;
        }
        TransactionDTO dto = new TransactionDTO();
        BeanUtils.copyProperties(record, dto);
        if (record.getTransactionStatus() != null) {
            dto.setTransactionStatus(record.getTransactionStatus().getCode());
        }
        return dto;
    }
}
