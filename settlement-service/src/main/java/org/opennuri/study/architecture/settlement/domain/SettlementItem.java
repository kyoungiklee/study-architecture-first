package org.opennuri.study.architecture.settlement.domain;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SettlementItem {
    String merchantId;
    BigDecimal amount;
    BigDecimal fee;
    BigDecimal netAmount;
    List<String> paymentIds;
}
