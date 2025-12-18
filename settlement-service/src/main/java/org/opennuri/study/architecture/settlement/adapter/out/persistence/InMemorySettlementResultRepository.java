package org.opennuri.study.architecture.settlement.adapter.out.persistence;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.architecture.settlement.domain.SettlementBatchRun;
import org.opennuri.study.architecture.settlement.port.out.SettlementResultRepositoryPort;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InMemorySettlementResultRepository implements SettlementResultRepositoryPort {

    private final List<SettlementBatchRun> store = new ArrayList<>();

    @Override
    public void saveRun(SettlementBatchRun run) {
        store.add(run);
        log.info("[InMemorySettlementResultRepository] saved run: {}", run);
    }
}
