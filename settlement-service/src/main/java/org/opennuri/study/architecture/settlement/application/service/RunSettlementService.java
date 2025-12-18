package org.opennuri.study.architecture.settlement.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.architecture.settlement.domain.SettlementBatchRun;
import org.opennuri.study.architecture.settlement.domain.SettlementItem;
import org.opennuri.study.architecture.settlement.domain.SettlementStatus;
import org.opennuri.study.architecture.settlement.port.in.RunSettlementUseCase;
import org.opennuri.study.architecture.settlement.port.out.LoadSettlementTargetsPort;
import org.opennuri.study.architecture.settlement.port.out.RequestFirmbankingPort;
import org.opennuri.study.architecture.settlement.port.out.SettlementResultRepositoryPort;
import org.opennuri.study.architecture.settlement.port.out.UpdatePaymentStatusPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunSettlementService implements RunSettlementUseCase {

    private final LoadSettlementTargetsPort loadSettlementTargetsPort;
    private final RequestFirmbankingPort requestFirmbankingPort;
    private final UpdatePaymentStatusPort updatePaymentStatusPort;
    private final SettlementResultRepositoryPort settlementResultRepositoryPort;

    @Override
    @Transactional
    public void runOnce(String runId) {
        String id = (runId == null || runId.isBlank()) ? UUID.randomUUID().toString() : runId;
        log.info("[Settlement] Run start. runId={}", id);
        settlementResultRepositoryPort.saveRun(SettlementBatchRun.builder()
                .runId(id)
                .startedAt(LocalDateTime.now())
                .status(SettlementStatus.RUNNING)
                .build());

        List<SettlementItem> targets = loadSettlementTargetsPort.loadTargets();
        log.info("[Settlement] Loaded targets: {}", targets.size());

        targets.forEach(item -> {
            boolean ok = requestFirmbankingPort.requestTransfer(item.getMerchantId(), item.getNetAmount());
            if (ok) {
                item.getPaymentIds().forEach(updatePaymentStatusPort::markSettled);
            }
        });

        settlementResultRepositoryPort.saveRun(SettlementBatchRun.builder()
                .runId(id)
                .startedAt(LocalDateTime.now())
                .status(SettlementStatus.SUCCEEDED)
                .build());

        log.info("[Settlement] Run finished. runId={}", id);
    }
}
