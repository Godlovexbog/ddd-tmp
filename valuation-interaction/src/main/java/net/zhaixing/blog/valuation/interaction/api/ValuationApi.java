package net.zhaixing.blog.valuation.interaction.api;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.zhaixing.blog.user.common.annotation.RateLimiter;
import net.zhaixing.blog.user.common.model.result.BaseResult;
import net.zhaixing.blog.user.common.model.result.Result;
import net.zhaixing.blog.valuation.application.command.ValuationAppService;
import net.zhaixing.blog.valuation.application.command.valuation.CreateValuationCommand;
import net.zhaixing.blog.valuation.application.query.model.valuation.ValuationDTO;
import net.zhaixing.blog.valuation.application.query.valuation.ValuationQueryService;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.service.ValuationCalculationService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 估值记录 API
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/valuation/")
public class ValuationApi {

    @Resource
    private ValuationQueryService valuationQueryService;

    @Resource
    private ValuationAppService valuationAppService;

    @Resource
    private ValuationCalculationService valuationCalculationService;

    /**
     * 创建估值记录
     * 限流: 每分钟最多 60 次
     */
    @RateLimiter(value = 60)
    @PostMapping("record")
    public Result<Void> create(@RequestBody @Valid CreateValuationCommand command) {
        valuationAppService.createValuation(command);
        return Result.ok(BaseResult.INSERT_SUCCESS);
    }

    /**
     * 根据ID查询估值记录
     */
    @GetMapping("record/{id}")
    public Result<ValuationDTO> getById(@PathVariable Long id) {
        ValuationDTO dto = valuationQueryService.getById(id);
        return Result.ok(dto);
    }

    /**
     * 根据项目ID查询估值记录
     */
    @GetMapping("record/project/{projectId}")
    public Result<List<ValuationDTO>> getByProjectId(@PathVariable Long projectId) {
        List<ValuationDTO> list = valuationQueryService.getByProjectId(projectId);
        return Result.ok(list);
    }

    /**
     * 根据项目代码查询估值记录
     */
    @GetMapping("record/code/{projectCode}")
    public Result<ValuationDTO> getByProjectCode(@PathVariable String projectCode) {
        ValuationDTO dto = valuationQueryService.getByProjectCode(projectCode);
        return Result.ok(dto);
    }

    /**
     * 删除估值记录
     */
    @DeleteMapping("record/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        valuationAppService.deleteValuation(id);
        return Result.ok(BaseResult.DELETE_SUCCESS);
    }

    /**
     * 计算实时估值-按项目ID
     * 限流: 每分钟最多 120 次
     */
    @RateLimiter(value = 120)
    @GetMapping("calculate/project/{projectId}")
    public Result<BigDecimal> calculateByProjectId(@PathVariable Long projectId) {
        BigDecimal amount = valuationCalculationService.calculateByProjectId(projectId);
        return Result.ok(amount);
    }

    /**
     * 计算实时估值-按项目代码
     * 限流: 每分钟最多 120 次
     */
    @RateLimiter(value = 120)
    @GetMapping("calculate/code/{projectCode}")
    public Result<BigDecimal> calculateByProjectCode(@PathVariable String projectCode) {
        BigDecimal amount = valuationCalculationService.calculateByProjectCode(projectCode);
        return Result.ok(amount);
    }
}
