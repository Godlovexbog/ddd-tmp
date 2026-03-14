package net.zhaixing.blog.valuation.application.query.transaction;

import net.zhaixing.blog.user.common.domain.AppQueryService;
import net.zhaixing.blog.valuation.application.query.model.transaction.TransactionDTO;

import java.util.List;

/**
 * 交易记录查询服务接口
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public interface TransactionQueryService extends AppQueryService {

    /**
     * 根据ID查询交易记录
     *
     * @param id 主键ID
     * @return 交易记录
     */
    TransactionDTO getById(Long id);

    /**
     * 根据标的ID查询交易记录
     *
     * @param targetId 标的ID
     * @return 交易记录列表
     */
    List<TransactionDTO> getByTargetId(Long targetId);
}
