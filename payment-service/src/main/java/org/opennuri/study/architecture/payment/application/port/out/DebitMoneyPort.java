package org.opennuri.study.architecture.payment.application.port.out;

public interface DebitMoneyPort {
    boolean debitMoney(Long memberId, Long amount);
}
