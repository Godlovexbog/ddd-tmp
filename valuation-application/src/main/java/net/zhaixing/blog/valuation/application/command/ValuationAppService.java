package net.zhaixing.blog.valuation.application.command;

import net.zhaixing.blog.user.common.domain.AppService;
import net.zhaixing.blog.valuation.application.command.delivery.CreateDeliveryCommand;
import net.zhaixing.blog.valuation.application.command.transaction.CreateTransactionCommand;

/**
 * 估值应用服务接口
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
public interface ValuationAppService extends AppService {

    /**
     * 创建估值记录
     *
     * @param command 创建命令
     */
    void createValuation(net.zhaixing.blog.valuation.application.command.valuation.CreateValuationCommand command);

    /**
     * 创建交易记录
     *
     * @param command 创建命令
     */
    void createTransaction(CreateTransactionCommand command);

    /**
     * 创建交付记录
     *
     * @param command 创建命令
     */
    void createDelivery(CreateDeliveryCommand command);

    /**
     * 删除估值记录
     *
     * @param id 记录ID
     */
    void deleteValuation(Long id);

    /**
     * 删除交易记录
     *
     * @param id 记录ID
     */
    void deleteTransaction(Long id);

    /**
     * 删除交付记录
     *
     * @param id 记录ID
     */
    void deleteDelivery(Long id);
}
