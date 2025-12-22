package org.opennuri.study.architecture.remittance.application.port.out;

public interface CommitMoneyPort {
    boolean commitMoney(String reservationId);
}
