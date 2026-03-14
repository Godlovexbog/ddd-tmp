package net.zhaixing.blog.valuation.interaction.api;

import net.zhaixing.blog.user.common.annotation.RateLimiter;
import net.zhaixing.blog.user.common.model.result.BaseResult;
import net.zhaixing.blog.user.common.model.result.Result;
import net.zhaixing.blog.valuation.application.command.ValuationAppService;
import net.zhaixing.blog.valuation.application.command.delivery.CreateDeliveryCommand;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 交付记录 API
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/valuation/")
public class DeliveryApi {

    private final ValuationAppService valuationAppService;

    public DeliveryApi(ValuationAppService valuationAppService) {
        this.valuationAppService = valuationAppService;
    }

    /**
     * 创建交付记录（现金/金融产品）
     * 限流: 每分钟最多 60 次
     */
    @RateLimiter(value = 60)
    @PostMapping("delivery")
    public Result<Void> create(@RequestBody @Valid CreateDeliveryCommand command) {
        valuationAppService.createDelivery(command);
        return Result.success(null);
    }

    /**
     * 删除交付记录
     */
    @DeleteMapping("delivery/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        valuationAppService.deleteDelivery(id);
        return Result.success(null);
    }
}
