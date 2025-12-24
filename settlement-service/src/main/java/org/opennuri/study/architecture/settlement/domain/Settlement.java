package org.opennuri.study.architecture.settlement.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class Settlement {
    private final String settlementId;
    private final LocalDateTime settlementDate;
    private final List<SettlementDetail> details;
    private SettlementStatus status;

    public void updateStatus(SettlementStatus status) {
        this.status = status;
    }
}
