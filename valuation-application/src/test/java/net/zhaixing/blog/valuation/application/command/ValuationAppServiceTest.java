package net.zhaixing.blog.valuation.application.command;

import net.zhaixing.blog.valuation.application.command.delivery.CreateDeliveryCommand;
import net.zhaixing.blog.valuation.application.command.impl.ValuationAppServiceImpl;
import net.zhaixing.blog.valuation.application.command.transaction.CreateTransactionCommand;
import net.zhaixing.blog.valuation.application.command.valuation.CreateValuationCommand;
import net.zhaixing.blog.valuation.domain.aggregate.delivery.repository.DeliveryRecordRepository;
import net.zhaixing.blog.valuation.domain.aggregate.transaction.repository.TransactionRecordRepository;
import net.zhaixing.blog.valuation.domain.aggregate.valuation.repository.ValuationRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 估值应用服务测试
 *
 * @author JanYork
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ValuationAppService 测试")
class ValuationAppServiceTest {

    @Mock
    private ValuationRecordRepository valuationRecordRepository;

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @Mock
    private DeliveryRecordRepository deliveryRecordRepository;

    private ValuationAppServiceImpl appService;

    @BeforeEach
    void setUp() {
        appService = new ValuationAppServiceImpl();
        setField(appService, "valuationRecordRepository", valuationRecordRepository);
        setField(appService, "transactionRecordRepository", transactionRecordRepository);
        setField(appService, "deliveryRecordRepository", deliveryRecordRepository);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("创建估值记录成功")
    void should_create_valuation_successfully() {
        CreateValuationCommand command = new CreateValuationCommand();
        command.setProjectId(1L);
        command.setProjectCode("P001");
        command.setTargetSource("银行理财");
        command.setTargetName("某银行理财产品");
        command.setTargetAmount(new BigDecimal("10000.00"));
        command.setValuationDate(LocalDate.of(2024, 1, 1));
        command.setTargetShare(new BigDecimal("10000.000000"));
        command.setAccountCode("ACC001");

        appService.createValuation(command);

        ArgumentCaptor captor = ArgumentCaptor.forClass(net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord.class);
        verify(valuationRecordRepository).save((net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord) captor.capture());
        
        net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord saved = 
                (net.zhaixing.blog.valuation.domain.aggregate.valuation.model.ValuationRecord) captor.getValue();
        
        assertThat(saved.getProjectId()).isEqualTo(1L);
        assertThat(saved.getProjectCode()).isEqualTo("P001");
        assertThat(saved.getTargetAmount()).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("创建交易记录成功")
    void should_create_transaction_successfully() {
        CreateTransactionCommand command = new CreateTransactionCommand();
        command.setTargetId(1L);
        command.setTargetCode("T001");
        command.setTargetSource("银行理财");
        command.setTransactionShare(new BigDecimal("2000.000000"));
        command.setTransactionAmount(new BigDecimal("2000.00"));
        command.setTransactionStatus("1");
        command.setTransactionDate(LocalDateTime.of(2024, 1, 15, 10, 0));

        appService.createTransaction(command);

        ArgumentCaptor captor = ArgumentCaptor.forClass(net.zhaixing.blog.valuation.domain.aggregate.transaction.model.TransactionRecord.class);
        verify(transactionRecordRepository).save((net.zhaixing.blog.valuation.domain.aggregate.transaction.model.TransactionRecord) captor.capture());
        
        net.zhaixing.blog.valuation.domain.aggregate.transaction.model.TransactionRecord saved = 
                (net.zhaixing.blog.valuation.domain.aggregate.transaction.model.TransactionRecord) captor.getValue();
        
        assertThat(saved.getTargetId()).isEqualTo(1L);
        assertThat(saved.getTransactionAmount()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("创建现金交付记录成功")
    void should_create_cash_delivery_successfully() {
        CreateDeliveryCommand command = new CreateDeliveryCommand();
        command.setDeliveryType("CASH");
        command.setProjectId(1L);
        command.setCashAmount(new BigDecimal("3000.00"));
        command.setDeliveryStatus("1");
        command.setDeliveryDate(LocalDateTime.of(2024, 2, 1, 10, 0));

        appService.createDelivery(command);

        verify(deliveryRecordRepository).save(any(net.zhaixing.blog.valuation.domain.aggregate.delivery.model.CashDeliveryRecord.class));
    }

    @Test
    @DisplayName("创建金融产品交付记录成功")
    void should_create_financial_delivery_successfully() {
        CreateDeliveryCommand command = new CreateDeliveryCommand();
        command.setDeliveryType("FINANCIAL");
        command.setProjectId(1L);
        command.setProductCode("FP001");
        command.setProductName("某基金产品");
        command.setDeliveryAmount(new BigDecimal("4000.00"));
        command.setDeliveryStatus("1");
        command.setDeliveryDate(LocalDateTime.of(2024, 3, 1, 10, 0));

        appService.createDelivery(command);

        verify(deliveryRecordRepository).saveFinancial(any(net.zhaixing.blog.valuation.domain.aggregate.delivery.model.FinancialProductDeliveryRecord.class));
    }

    @Test
    @DisplayName("删除估值记录成功")
    void should_delete_valuation_successfully() {
        appService.deleteValuation(1L);

        verify(valuationRecordRepository).delete(1L);
    }

    @Test
    @DisplayName("删除交易记录成功")
    void should_delete_transaction_successfully() {
        appService.deleteTransaction(1L);

        verify(transactionRecordRepository).delete(1L);
    }

    @Test
    @DisplayName("删除交付记录成功")
    void should_delete_delivery_successfully() {
        appService.deleteDelivery(1L);

        verify(deliveryRecordRepository).delete(1L);
    }
}
