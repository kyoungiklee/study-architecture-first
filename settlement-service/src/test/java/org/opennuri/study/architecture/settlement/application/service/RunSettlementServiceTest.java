package org.opennuri.study.architecture.settlement.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opennuri.study.architecture.settlement.domain.PaymentData;
import org.opennuri.study.architecture.settlement.domain.Settlement;
import org.opennuri.study.architecture.settlement.domain.SettlementStatus;
import org.opennuri.study.architecture.settlement.port.out.LoadPaymentDataPort;
import org.opennuri.study.architecture.settlement.port.out.RequestFirmbankingPort;
import org.opennuri.study.architecture.settlement.port.out.SettlementResultRepositoryPort;
import org.opennuri.study.architecture.settlement.port.out.UpdatePaymentStatusPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RunSettlementServiceTest {

    private RunSettlementService runSettlementService;
    private LoadPaymentDataPort loadPaymentDataPort;
    private FeeCalculator feeCalculator;
    private RequestFirmbankingPort requestFirmbankingPort;
    private UpdatePaymentStatusPort updatePaymentStatusPort;
    private SettlementResultRepositoryPort settlementResultRepositoryPort;

    @BeforeEach
    void setUp() {
        loadPaymentDataPort = Mockito.mock(LoadPaymentDataPort.class);
        feeCalculator = new DefaultFeeCalculator();
        requestFirmbankingPort = Mockito.mock(RequestFirmbankingPort.class);
        updatePaymentStatusPort = Mockito.mock(UpdatePaymentStatusPort.class);
        settlementResultRepositoryPort = Mockito.mock(SettlementResultRepositoryPort.class);

        runSettlementService = new RunSettlementService(
                loadPaymentDataPort,
                feeCalculator,
                requestFirmbankingPort,
                updatePaymentStatusPort,
                settlementResultRepositoryPort
        );
    }

    @Test
    @DisplayName("정상적인 정산 흐름 테스트")
    void testRunOnce_Success() {
        // given
        String runId = "test-run-1";
        given(settlementResultRepositoryPort.findSettlementById(runId)).willReturn(Optional.empty());
        
        List<PaymentData> payments = List.of(
                PaymentData.builder().paymentId("P1").merchantId("M1").amount(new BigDecimal("10000")).paymentDateTime(LocalDateTime.now()).build()
        );
        given(loadPaymentDataPort.loadCompletedPayments()).willReturn(payments);
        given(requestFirmbankingPort.requestTransfer(anyString(), any(BigDecimal.class))).willReturn(true);

        // when
        runSettlementService.runOnce(runId);

        // then
        verify(requestFirmbankingPort, times(1)).requestTransfer(eq("M1"), any(BigDecimal.class));
        verify(updatePaymentStatusPort, times(1)).markSettled("P1");
        verify(settlementResultRepositoryPort).saveSettlement(argThat(s -> s.getStatus() == SettlementStatus.SUCCESS));
    }

    @Test
    @DisplayName("가맹점 이체 실패 시 FAILED 상태 저장 및 재실행 시 성공 테스트")
    void testRunOnce_Retry() {
        // 1. 첫 번째 실행 - 실패
        String runId = "test-run-2";
        given(settlementResultRepositoryPort.findSettlementById(runId)).willReturn(Optional.empty());
        
        List<PaymentData> payments = List.of(
                PaymentData.builder().paymentId("P1").merchantId("M1").amount(new BigDecimal("10000")).paymentDateTime(LocalDateTime.now()).build()
        );
        given(loadPaymentDataPort.loadCompletedPayments()).willReturn(payments);
        given(requestFirmbankingPort.requestTransfer(anyString(), any(BigDecimal.class))).willReturn(false); // 실패 시뮬레이션

        runSettlementService.runOnce(runId);

        // 2. 두 번째 실행 - 재시도 (기존 데이터 존재 시뮬레이션)
        // 실제로는 settlementResultRepositoryPort가 저장된걸 반환해야 하므로 mock 설정 변경
        Settlement failedSettlement = Settlement.builder()
                .settlementId(runId)
                .details(List.of(org.opennuri.study.architecture.settlement.domain.SettlementDetail.create("M1", payments)))
                .status(SettlementStatus.FAILED)
                .build();
        failedSettlement.getDetails().get(0).calculateFee(new BigDecimal("0.03"));
        failedSettlement.getDetails().get(0).updateStatus(SettlementStatus.FAILED);

        given(settlementResultRepositoryPort.findSettlementById(runId)).willReturn(Optional.of(failedSettlement));
        given(requestFirmbankingPort.requestTransfer(anyString(), any(BigDecimal.class))).willReturn(true); // 이번엔 성공

        runSettlementService.runOnce(runId);

        // then
        verify(requestFirmbankingPort, times(2)).requestTransfer(eq("M1"), any(BigDecimal.class));
        verify(updatePaymentStatusPort, times(1)).markSettled("P1"); // 두 번째 성공 시에만 호출됨
    }
}
