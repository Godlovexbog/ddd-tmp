package net.zhaixing.blog.valuation.infra.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zhaixing.blog.valuation.infra.db.model.ValuationRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 估值记录 Mapper 接口
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Mapper
public interface ValuationRecordMapper extends BaseMapper<ValuationRecordPO> {

    /**
     * 根据项目ID查询估值记录
     *
     * @param projectId 项目ID
     * @return 估值记录列表
     */
    @Select("SELECT * FROM t_valuation_record WHERE project_id = #{projectId} AND is_deleted = 0")
    List<ValuationRecordPO> selectByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据项目代码查询估值记录
     *
     * @param projectCode 项目代码
     * @return 估值记录
     */
    @Select("SELECT * FROM t_valuation_record WHERE project_code = #{projectCode} AND is_deleted = 0 LIMIT 1")
    ValuationRecordPO selectByProjectCode(@Param("projectCode") String projectCode);
}
