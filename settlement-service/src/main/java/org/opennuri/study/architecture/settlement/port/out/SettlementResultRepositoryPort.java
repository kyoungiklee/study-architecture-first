package org.opennuri.study.architecture.settlement.port.out;

import org.opennuri.study.architecture.settlement.domain.SettlementBatchRun;

public interface SettlementResultRepositoryPort {
    void saveRun(SettlementBatchRun run);
}
