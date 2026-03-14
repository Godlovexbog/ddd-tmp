package net.zhaixing.blog.valuation.interaction.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.zhaixing.blog.user.common.model.result.Result;
import net.zhaixing.blog.valuation.application.command.ValuationAppService;
import net.zhaixing.blog.valuation.application.command.valuation.CreateValuationCommand;
import net.zhaixing.blog.valuation.application.query.model.valuation.ValuationDTO;
import net.zhaixing.blog.valuation.application.query.valuation.ValuationQueryService;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.service.ValuationCalculationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 估值 API 集成测试
 *
 * @author JanYork
 */
@WebMvcTest(ValuationApi.class)
@DisplayName("ValuationApi 集成测试")
class ValuationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ValuationQueryService valuationQueryService;

    @MockBean
    private ValuationAppService valuationAppService;

    @MockBean
    private ValuationCalculationService valuationCalculationService;

    @Test
    @DisplayName("创建估值记录 - 成功场景")
    void should_return_ok_when_create_valuation_success() throws Exception {
        CreateValuationCommand command = new CreateValuationCommand();
        command.setProjectId(1L);
        command.setProjectCode("P001");
        command.setTargetSource("银行理财");
        command.setTargetName("某银行理财产品");
        command.setTargetAmount(new BigDecimal("10000.00"));
        command.setValuationDate(LocalDate.of(2024, 1, 1));
        command.setTargetShare(new BigDecimal("10000.000000"));
        command.setAccountCode("ACC001");

        mockMvc.perform(post("/api/valuation/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    @DisplayName("创建估值 - 参数校验失败（projectId为空）")
    void should_return_error_when_project_id_is_null() throws Exception {
        CreateValuationCommand command = new CreateValuationCommand();
        command.setProjectCode("P001");
        command.setTargetAmount(new BigDecimal("10000.00"));

        mockMvc.perform(post("/api/valuation/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("创建估值 - 参数校验失败（targetAmount为负数）")
    void should_return_error_when_target_amount_is_negative() throws Exception {
        CreateValuationCommand command = new CreateValuationCommand();
        command.setProjectId(1L);
        command.setProjectCode("P001");
        command.setTargetAmount(new BigDecimal("-1000.00"));

        mockMvc.perform(post("/api/valuation/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("查询估值 - 记录存在")
    void should_return_valuation_when_record_exists() throws Exception {
        ValuationDTO dto = new ValuationDTO();
        dto.setId(1L);
        dto.setProjectId(1L);
        dto.setProjectCode("P001");
        dto.setTargetAmount(new BigDecimal("10000.00"));
        dto.setValuationDate(LocalDate.of(2024, 1, 1));

        when(valuationQueryService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/valuation/record/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.projectCode").value("P001"));
    }

    @Test
    @DisplayName("查询估值 - 记录不存在")
    void should_return_null_when_record_not_found() throws Exception {
        when(valuationQueryService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/valuation/record/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("根据项目ID查询估值列表")
    void should_return_list_when_query_by_project_id() throws Exception {
        ValuationDTO dto = new ValuationDTO();
        dto.setId(1L);
        dto.setProjectId(1L);
        dto.setProjectCode("P001");

        when(valuationQueryService.getByProjectId(1L)).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/valuation/record/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("根据项目代码查询估值")
    void should_return_valuation_when_query_by_project_code() throws Exception {
        ValuationDTO dto = new ValuationDTO();
        dto.setId(1L);
        dto.setProjectCode("P001");
        dto.setTargetAmount(new BigDecimal("10000.00"));

        when(valuationQueryService.getByProjectCode("P001")).thenReturn(dto);

        mockMvc.perform(get("/api/valuation/record/code/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.projectCode").value("P001"));
    }

    @Test
    @DisplayName("删除估值记录 - 成功")
    void should_return_ok_when_delete_success() throws Exception {
        mockMvc.perform(delete("/api/valuation/record/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    @DisplayName("计算估值 - 按项目ID")
    void should_calculate_by_project_id() throws Exception {
        when(valuationCalculationService.calculateByProjectId(1L))
                .thenReturn(new BigDecimal("10000.00"));

        mockMvc.perform(get("/api/valuation/calculate/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(10000.00));
    }

    @Test
    @DisplayName("计算估值 - 按项目代码")
    void should_calculate_by_project_code() throws Exception {
        when(valuationCalculationService.calculateByProjectCode("P001"))
                .thenReturn(new BigDecimal("19000.00"));

        mockMvc.perform(get("/api/valuation/calculate/code/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(19000.00));
    }
}
