package net.zhaixing.blog.valuation.application.query.valuation;

import net.zhaixing.blog.user.common.domain.AppQueryService;
import net.zhaixing.blog.valuation.application.query.model.valuation.ValuationDTO;

import java.util.List;

/**
 * 估值记录查询服务接口
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public interface ValuationQueryService extends AppQueryService {

    /**
     * 根据ID查询估值记录
     *
     * @param id 主键ID
     * @return 估值记录
     */
    ValuationDTO getById(Long id);

    /**
     * 根据项目ID查询估值记录
     *
     * @param projectId 项目ID
     * @return 估值记录列表
     */
    List<ValuationDTO> getByProjectId(Long projectId);

    /**
     * 根据项目代码查询估值记录
     *
     * @param projectCode 项目代码
     * @return 估值记录
     */
    ValuationDTO getByProjectCode(String projectCode);
}
