package org.opennuri.study.architecture.settlement.port.out;

import org.opennuri.study.architecture.settlement.domain.Settlement;
import org.opennuri.study.architecture.settlement.domain.SettlementBatchRun;

import java.util.Optional;

public interface SettlementResultRepositoryPort {
    void saveRun(SettlementBatchRun run);
    void saveSettlement(Settlement settlement);
    Optional<Settlement> findSettlementById(String settlementId);
}
