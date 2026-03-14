package net.zhaixing.blog.valuation.interaction.api;

import net.zhaixing.blog.user.common.annotation.RateLimiter;
import net.zhaixing.blog.user.common.model.result.BaseResult;
import net.zhaixing.blog.user.common.model.result.Result;
import net.zhaixing.blog.valuation.application.command.ValuationAppService;
import net.zhaixing.blog.valuation.application.command.transaction.CreateTransactionCommand;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 交易记录 API
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/valuation/")
public class TransactionApi {

    private final ValuationAppService valuationAppService;

    public TransactionApi(ValuationAppService valuationAppService) {
        this.valuationAppService = valuationAppService;
    }

    /**
     * 创建交易记录
     * 限流: 每分钟最多 60 次
     */
    @RateLimiter(value = 60)
    @PostMapping("transaction")
    public Result<Void> create(@RequestBody @Valid CreateTransactionCommand command) {
        valuationAppService.createTransaction(command);
        return Result.success(null);
    }

    /**
     * 删除交易记录
     */
    @DeleteMapping("transaction/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        valuationAppService.deleteTransaction(id);
        return Result.success(null);
    }
}
