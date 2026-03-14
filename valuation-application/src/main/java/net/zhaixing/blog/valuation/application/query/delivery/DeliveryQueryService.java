package net.zhaixing.blog.valuation.application.query.delivery;

import net.zhaixing.blog.user.common.domain.AppQueryService;
import net.zhaixing.blog.valuation.application.query.model.delivery.DeliveryDTO;

import java.util.List;

/**
 * 交付记录查询服务接口
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public interface DeliveryQueryService extends AppQueryService {

    /**
     * 根据ID查询交付记录
     *
     * @param id 主键ID
     * @return 交付记录
     */
    DeliveryDTO getById(Long id);

    /**
     * 根据项目ID查询现金交付记录
     *
     * @param projectId 项目ID
     * @return 交付记录列表
     */
    List<DeliveryDTO> getCashByProjectId(Long projectId);
}
