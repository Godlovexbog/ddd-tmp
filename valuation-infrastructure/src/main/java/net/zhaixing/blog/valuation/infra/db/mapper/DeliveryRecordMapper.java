package net.zhaixing.blog.valuation.infra.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zhaixing.blog.valuation.infra.db.model.CashDeliveryRecordPO;
import net.zhaixing.blog.valuation.infra.db.model.FinancialProductDeliveryRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交付记录 Mapper 接口
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Mapper
public interface DeliveryRecordMapper extends BaseMapper<CashDeliveryRecordPO> {

    /**
     * 根据项目ID查询现金交付记录
     *
     * @param projectId 项目ID
     * @return 现金交付记录列表
     */
    @Select("SELECT * FROM t_cash_delivery_record WHERE project_id = #{projectId} AND is_deleted = 0")
    List<CashDeliveryRecordPO> selectCashByProjectId(@Param("projectId") Long projectId);

    /**
     * 查询指定日期之后的成功现金交付记录
     *
     * @param afterDate 日期
     * @param status   状态
     * @return 现金交付记录列表
     */
    @Select("SELECT * FROM t_cash_delivery_record WHERE delivery_date >= #{afterDate} AND delivery_status = #{status} AND is_deleted = 0")
    List<CashDeliveryRecordPO> selectCashByDateAfterAndStatus(@Param("afterDate") LocalDateTime afterDate, @Param("status") String status);

    /**
     * 查询指定日期之后的成功金融产品交付记录
     *
     * @param afterDate 日期
     * @param status   状态
     * @return 金融产品交付记录列表
     */
    @Select("SELECT * FROM t_financial_product_delivery_record WHERE delivery_date >= #{afterDate} AND delivery_status = #{status} AND is_deleted = 0")
    List<FinancialProductDeliveryRecordPO> selectFinancialByDateAfterAndStatus(@Param("afterDate") LocalDateTime afterDate, @Param("status") String status);

    /**
     * 插入金融产品交付记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertFinancial(FinancialProductDeliveryRecordPO record);
}
