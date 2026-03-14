package net.zhaixing.blog.valuation.application.query;

import net.zhaixing.blog.valuation.application.query.impl.ValuationQueryServiceImpl;
import net.zhaixing.blog.valuation.application.query.model.valuation.ValuationDTO;
import net.zhaixing.blog.valuation.application.query.valuation.ValuationQueryService;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.repository.ValuationRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 估值查询服务测试
 *
 * @author JanYork
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ValuationQueryService 测试")
class ValuationQueryServiceTest {

    @Mock
    private ValuationRecordRepository valuationRecordRepository;

    private ValuationQueryServiceImpl queryService;

    @BeforeEach
    void setUp() {
        queryService = new ValuationQueryServiceImpl(valuationRecordRepository);
    }

    @Test
    @DisplayName("根据ID查询估值记录")
    void should_get_by_id() {
        ValuationRecord record = ValuationRecord.builder()
                .id(1L)
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        when(valuationRecordRepository.byId(1L)).thenReturn(record);

        ValuationDTO result = queryService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProjectCode()).isEqualTo("P001");
    }

    @Test
    @DisplayName("根据ID查询不存在的记录返回null")
    void should_return_null_when_id_not_found() {
        when(valuationRecordRepository.byId(999L)).thenReturn(null);

        ValuationDTO result = queryService.getById(999L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("根据项目ID查询估值记录")
    void should_get_by_project_id() {
        ValuationRecord record1 = ValuationRecord.builder()
                .id(1L)
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        ValuationRecord record2 = ValuationRecord.builder()
                .id(2L)
                .projectId(1L)
                .projectCode("P002")
                .targetAmount(new BigDecimal("20000.00"))
                .valuationDate(LocalDate.of(2024, 2, 1))
                .build();

        when(valuationRecordRepository.byProjectId(1L)).thenReturn(Arrays.asList(record1, record2));

        List<ValuationDTO> results = queryService.getByProjectId(1L);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getProjectCode()).isEqualTo("P001");
        assertThat(results.get(1).getProjectCode()).isEqualTo("P002");
    }

    @Test
    @DisplayName("根据项目ID查询无记录返回空列表")
    void should_return_empty_list_when_project_id_not_found() {
        when(valuationRecordRepository.byProjectId(999L)).thenReturn(Collections.emptyList());

        List<ValuationDTO> results = queryService.getByProjectId(999L);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("根据项目代码查询估值记录")
    void should_get_by_project_code() {
        ValuationRecord record = ValuationRecord.builder()
                .id(1L)
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        when(valuationRecordRepository.byProjectCode("P001")).thenReturn(record);

        ValuationDTO result = queryService.getByProjectCode("P001");

        assertThat(result).isNotNull();
        assertThat(result.getProjectCode()).isEqualTo("P001");
        assertThat(result.getTargetAmount()).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("根据项目代码查询不存在的记录返回null")
    void should_return_null_when_project_code_not_found() {
        when(valuationRecordRepository.byProjectCode("NOT_EXIST")).thenReturn(null);

        ValuationDTO result = queryService.getByProjectCode("NOT_EXIST");

        assertThat(result).isNull();
    }
}
