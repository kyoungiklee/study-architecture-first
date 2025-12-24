package org.opennuri.study.architecture.settlement.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SettlementBatchRun {
    String runId;
    LocalDateTime startedAt;
    SettlementStatus status;
}
