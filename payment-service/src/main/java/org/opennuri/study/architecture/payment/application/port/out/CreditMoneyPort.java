package org.opennuri.study.architecture.payment.application.port.out;

public interface CreditMoneyPort {
    boolean creditMoney(Long memberId, Long amount);
}
