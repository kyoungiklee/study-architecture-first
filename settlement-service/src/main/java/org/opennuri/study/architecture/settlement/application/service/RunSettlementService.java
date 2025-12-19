package org.opennuri.study.architecture.settlement.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.architecture.settlement.domain.*;
import org.opennuri.study.architecture.settlement.port.in.RunSettlementUseCase;
import org.opennuri.study.architecture.settlement.port.out.LoadPaymentDataPort;
import org.opennuri.study.architecture.settlement.port.out.RequestFirmbankingPort;
import org.opennuri.study.architecture.settlement.port.out.SettlementResultRepositoryPort;
import org.opennuri.study.architecture.settlement.port.out.UpdatePaymentStatusPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunSettlementService implements RunSettlementUseCase {

    private final LoadPaymentDataPort loadPaymentDataPort;
    private final FeeCalculator feeCalculator;
    private final RequestFirmbankingPort requestFirmbankingPort;
    private final UpdatePaymentStatusPort updatePaymentStatusPort;
    private final SettlementResultRepositoryPort settlementResultRepositoryPort;

    @Override
    @Transactional
    public void runOnce(String runId) {
        String id = (runId == null || runId.isBlank()) ? UUID.randomUUID().toString() : runId;
        log.info("[Settlement] Run start. runId={}", id);

        // Idempotency: 기존 정산 결과 확인
        Optional<Settlement> existingSettlement = settlementResultRepositoryPort.findSettlementById(id);
        
        Settlement settlement;
        if (existingSettlement.isPresent()) {
            log.info("[Settlement] Existing settlement found. runId={}", id);
            settlement = existingSettlement.get();
        } else {
            // 1. 수집
            List<PaymentData> payments = loadPaymentDataPort.loadCompletedPayments();
            
            // 2. 그룹핑 및 계산
            Map<String, List<PaymentData>> groupedPayments = payments.stream()
                    .collect(Collectors.groupingBy(PaymentData::getMerchantId));

            List<SettlementDetail> details = groupedPayments.entrySet().stream()
                    .map(entry -> {
                        String merchantId = entry.getKey();
                        List<PaymentData> merchantPayments = entry.getValue();
                        SettlementDetail detail = SettlementDetail.create(merchantId, merchantPayments);
                        
                        BigDecimal feeRate = feeCalculator.calculateFeeRate(merchantId);
                        detail.calculateFee(feeRate);
                        return detail;
                    }).toList();

            settlement = Settlement.builder()
                    .settlementId(id)
                    .settlementDate(LocalDateTime.now())
                    .details(details)
                    .status(SettlementStatus.PROCESSING)
                    .build();
        }

        // 3. 실행 (가맹점 단위)
        for (SettlementDetail detail : settlement.getDetails()) {
            // 이미 성공한 정산은 건너뜀 (가맹점 단위 재실행 전략)
            if (detail.getStatus() == SettlementStatus.SUCCESS) {
                log.info("[Settlement] Merchant already settled. merchantId={}", detail.getMerchantId());
                continue;
            }

            try {
                log.info("[Settlement] Processing merchant: {}, amount: {}", detail.getMerchantId(), detail.getNetAmount());
                boolean ok = requestFirmbankingPort.requestTransfer(detail.getMerchantId(), detail.getNetAmount());
                if (ok) {
                    detail.updateStatus(SettlementStatus.SUCCESS);
                    detail.getPaymentIds().forEach(updatePaymentStatusPort::markSettled);
                } else {
                    detail.updateStatus(SettlementStatus.FAILED);
                }
            } catch (Exception e) {
                log.error("[Settlement] Failed to process merchant: {}", detail.getMerchantId(), e);
                detail.updateStatus(SettlementStatus.FAILED);
            }
        }

        // 4. 최종 상태 결정 및 저장
        boolean allSuccess = settlement.getDetails().stream()
                .allMatch(d -> d.getStatus() == SettlementStatus.SUCCESS);
        settlement.updateStatus(allSuccess ? SettlementStatus.SUCCESS : SettlementStatus.FAILED);

        settlementResultRepositoryPort.saveSettlement(settlement);

        log.info("[Settlement] Run finished. runId={}, status={}", id, settlement.getStatus());
    }
}
