package org.opennuri.study.architecture.settlement.port.out;

import java.math.BigDecimal;

public interface RequestFirmbankingPort {
    boolean requestTransfer(String merchantId, BigDecimal amount);
}
