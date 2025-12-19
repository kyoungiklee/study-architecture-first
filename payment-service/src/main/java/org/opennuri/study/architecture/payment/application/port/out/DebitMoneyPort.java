package org.opennuri.study.architecture.payment.application.port.out;

/**
 * 사용자 잔액 차감을 위한 출력 포트 인터페이스
 */
public interface DebitMoneyPort {
    /**
     * 사용자의 잔액을 차감합니다.
     *
     * @param memberId 사용자(멤버) ID
     * @param amount 차감할 금액
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    boolean debitMoney(Long memberId, Long amount);
}
