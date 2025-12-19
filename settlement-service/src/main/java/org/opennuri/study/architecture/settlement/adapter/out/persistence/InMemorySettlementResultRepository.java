package org.opennuri.study.architecture.settlement.adapter.out.persistence;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.architecture.settlement.domain.Settlement;
import org.opennuri.study.architecture.settlement.domain.SettlementBatchRun;
import org.opennuri.study.architecture.settlement.port.out.SettlementResultRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemorySettlementResultRepository implements SettlementResultRepositoryPort {

    private final List<SettlementBatchRun> runStore = new ArrayList<>();
    private final Map<String, Settlement> settlementStore = new ConcurrentHashMap<>();

    @Override
    public void saveRun(SettlementBatchRun run) {
        runStore.add(run);
        log.info("[InMemorySettlementResultRepository] saved run: {}", run);
    }

    @Override
    public void saveSettlement(Settlement settlement) {
        settlementStore.put(settlement.getSettlementId(), settlement);
        log.info("[InMemorySettlementResultRepository] saved settlement: {}", settlement.getSettlementId());
    }

    @Override
    public Optional<Settlement> findSettlementById(String settlementId) {
        return Optional.ofNullable(settlementStore.get(settlementId));
    }
}
