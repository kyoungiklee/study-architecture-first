package org.opennuri.study.architecture.remittance.application.port.out;

public interface RollbackMoneyPort {
    boolean rollbackMoney(String reservationId, String reason);
}
