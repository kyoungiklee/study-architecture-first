package org.opennuri.study.architecture.payment.application.port.out;

/**
 * 사용자 잔액 증액(충전)을 위한 출력 포트 인터페이스
 */
public interface CreditMoneyPort {
    /**
     * 사용자의 잔액을 증액합니다.
     *
     * @param memberId 사용자(멤버) ID
     * @param amount 증액할 금액
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    boolean creditMoney(Long memberId, Long amount);
}
