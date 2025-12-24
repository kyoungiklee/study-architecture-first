package org.opennuri.study.architecture.banking.application.port.out;

import java.math.BigDecimal;

public interface ExternalBankPort {
    boolean transfer(String fromBankAccount, String toBankAccount, BigDecimal amount);
}
