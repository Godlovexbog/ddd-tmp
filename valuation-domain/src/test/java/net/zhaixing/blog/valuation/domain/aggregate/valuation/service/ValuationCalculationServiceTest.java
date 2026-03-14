package net.zhaixing.blog.valuation.domain.aggregate.valuation.service;

import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.CashDeliveryRecord;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.model.FinancialProductDeliveryRecord;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.repository.DeliveryRecordRepository;
import net.zhaixing.blog.valuation.domain.aggregate.transaction.model.TransactionRecord;
import net.zhaixing.blog.valuation.domain.aggregate.transaction.repository.TransactionRecordRepository;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.repository.ValuationRecordRepository;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.service.impl.ValuationCalculationServiceImpl;
import net.zhaixing.blog.valuation.domain.share.enums.TransactionStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * 估值计算服务单元测试
 *
 * @author JanYork
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ValuationCalculationService 单元测试")
class ValuationCalculationServiceTest {

    @Mock
    private ValuationRecordRepository valuationRecordRepository;

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @Mock
    private DeliveryRecordRepository deliveryRecordRepository;

    private ValuationCalculationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ValuationCalculationServiceImpl(
                valuationRecordRepository,
                transactionRecordRepository,
                deliveryRecordRepository
        );
    }

    @Test
    @DisplayName("无交易记录时返回基础估值金额")
    void should_return_base_amount_when_no_transactions() {
        ValuationRecord record = ValuationRecord.builder()
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        when(transactionRecordRepository.listByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listCashByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listFinancialByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());

        BigDecimal result = service.calculate(record);

        assertThat(result).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("成功交易时累加交易金额")
    void should_add_transaction_amount_when_success_transactions() {
        ValuationRecord record = ValuationRecord.builder()
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        TransactionRecord transaction = TransactionRecord.builder()
                .targetId(1L)
                .transactionAmount(new BigDecimal("2000.00"))
                .transactionStatus(TransactionStatusEnum.SUCCESS)
                .transactionDate(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        when(transactionRecordRepository.listByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Arrays.asList(transaction));
        when(deliveryRecordRepository.listCashByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listFinancialByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());

        BigDecimal result = service.calculate(record);

        assertThat(result).isEqualByComparingTo(new BigDecimal("12000.00"));
    }

    @Test
    @DisplayName("失败交易应被忽略")
    void should_ignore_failed_transactions() {
        ValuationRecord record = ValuationRecord.builder()
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        TransactionRecord failedTransaction = TransactionRecord.builder()
                .targetId(1L)
                .transactionAmount(new BigDecimal("5000.00"))
                .transactionStatus(TransactionStatusEnum.FAILED)
                .transactionDate(LocalDateTime.of(2024, 1, 10, 10, 0))
                .build();

        when(transactionRecordRepository.listByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listCashByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listFinancialByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());

        BigDecimal result = service.calculate(record);

        assertThat(result).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("处理中交易应被忽略")
    void should_ignore_pending_transactions() {
        ValuationRecord record = ValuationRecord.builder()
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        when(transactionRecordRepository.listByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listCashByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listFinancialByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());

        BigDecimal result = service.calculate(record);

        assertThat(result).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("成功现金交付时累加交付金额")
    void should_add_cash_delivery_amount_when_success() {
        ValuationRecord record = ValuationRecord.builder()
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        CashDeliveryRecord cashDelivery = CashDeliveryRecord.builder()
                .projectId(1L)
                .cashAmount(new BigDecimal("3000.00"))
                .deliveryStatus(TransactionStatusEnum.SUCCESS)
                .deliveryDate(LocalDateTime.of(2024, 2, 1, 10, 0))
                .build();

        when(transactionRecordRepository.listByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listCashByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Arrays.asList(cashDelivery));
        when(deliveryRecordRepository.listFinancialByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());

        BigDecimal result = service.calculate(record);

        assertThat(result).isEqualByComparingTo(new BigDecimal("13000.00"));
    }

    @Test
    @DisplayName("成功金融产品交付时累加交付金额")
    void should_add_financial_delivery_amount_when_success() {
        ValuationRecord record = ValuationRecord.builder()
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        FinancialProductDeliveryRecord financialDelivery = FinancialProductDeliveryRecord.builder()
                .projectId(1L)
                .deliveryAmount(new BigDecimal("4000.00"))
                .deliveryStatus(TransactionStatusEnum.SUCCESS)
                .deliveryDate(LocalDateTime.of(2024, 3, 1, 10, 0))
                .build();

        when(transactionRecordRepository.listByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listCashByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listFinancialByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Arrays.asList(financialDelivery));

        BigDecimal result = service.calculate(record);

        assertThat(result).isEqualByComparingTo(new BigDecimal("14000.00"));
    }

    @Test
    @DisplayName("综合计算所有成功金额")
    void should_calculate_total_with_all_components() {
        ValuationRecord record = ValuationRecord.builder()
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        TransactionRecord transaction = TransactionRecord.builder()
                .targetId(1L)
                .transactionAmount(new BigDecimal("2000.00"))
                .transactionStatus(TransactionStatusEnum.SUCCESS)
                .transactionDate(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        CashDeliveryRecord cashDelivery = CashDeliveryRecord.builder()
                .projectId(1L)
                .cashAmount(new BigDecimal("3000.00"))
                .deliveryStatus(TransactionStatusEnum.SUCCESS)
                .deliveryDate(LocalDateTime.of(2024, 2, 1, 10, 0))
                .build();

        FinancialProductDeliveryRecord financialDelivery = FinancialProductDeliveryRecord.builder()
                .projectId(1L)
                .deliveryAmount(new BigDecimal("4000.00"))
                .deliveryStatus(TransactionStatusEnum.SUCCESS)
                .deliveryDate(LocalDateTime.of(2024, 3, 1, 10, 0))
                .build();

        when(transactionRecordRepository.listByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Arrays.asList(transaction));
        when(deliveryRecordRepository.listCashByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Arrays.asList(cashDelivery));
        when(deliveryRecordRepository.listFinancialByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Arrays.asList(financialDelivery));

        BigDecimal result = service.calculate(record);

        assertThat(result).isEqualByComparingTo(new BigDecimal("19000.00"));
    }

    @Test
    @DisplayName("空估值记录返回0")
    void should_return_zero_when_valuation_record_is_null() {
        BigDecimal result = service.calculate(null);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("按项目ID计算估值")
    void should_calculate_by_project_id() {
        ValuationRecord record = ValuationRecord.builder()
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        when(valuationRecordRepository.byProjectId(1L)).thenReturn(Arrays.asList(record));
        when(transactionRecordRepository.listByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listCashByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listFinancialByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());

        BigDecimal result = service.calculateByProjectId(1L);

        assertThat(result).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("按项目代码计算估值")
    void should_calculate_by_project_code() {
        ValuationRecord record = ValuationRecord.builder()
                .projectId(1L)
                .projectCode("P001")
                .targetAmount(new BigDecimal("10000.00"))
                .valuationDate(LocalDate.of(2024, 1, 1))
                .build();

        when(valuationRecordRepository.byProjectCode("P001")).thenReturn(record);
        when(transactionRecordRepository.listByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listCashByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());
        when(deliveryRecordRepository.listFinancialByDateAfterAndStatus(any(), eq(TransactionStatusEnum.SUCCESS)))
                .thenReturn(Collections.emptyList());

        BigDecimal result = service.calculateByProjectCode("P001");

        assertThat(result).isEqualByComparingTo(new BigDecimal("10000.00"));
    }
}
