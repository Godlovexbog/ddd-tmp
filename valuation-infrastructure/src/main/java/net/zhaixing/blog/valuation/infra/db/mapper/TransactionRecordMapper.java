package net.zhaixing.blog.valuation.infra.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zhaixing.blog.valuation.infra.db.model.TransactionRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易记录 Mapper 接口
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Mapper
public interface TransactionRecordMapper extends BaseMapper<TransactionRecordPO> {

    /**
     * 根据标的ID查询交易记录
     *
     * @param targetId 标的ID
     * @return 交易记录列表
     */
    @Select("SELECT * FROM t_transaction_record WHERE target_id = #{targetId} AND is_deleted = 0")
    List<TransactionRecordPO> selectByTargetId(@Param("targetId") Long targetId);

    /**
     * 查询指定日期之后的成功交易记录
     *
     * @param afterDate 日期
     * @param status   状态
     * @return 交易记录列表
     */
    @Select("SELECT * FROM t_transaction_record WHERE transaction_date >= #{afterDate} AND transaction_status = #{status} AND is_deleted = 0")
    List<TransactionRecordPO> selectByDateAfterAndStatus(@Param("afterDate") LocalDateTime afterDate, @Param("status") String status);
}
