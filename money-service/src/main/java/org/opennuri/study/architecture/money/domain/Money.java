package org.opennuri.study.architecture.money.domain;

import lombok.Value;

/**
 * Money 도메인 모델 (Hexagonal 아키텍처의 도메인 계층)
 * 선불 잔액을 관리하는 순수 도메인 객체로서,
 * 프레임워크나 영속성(JPA 등) 관련 어노테이션을 포함하지 않습니다.
 *
 * 책임:
 * - 선불 잔액 관리
 * - 충전/차감 시 정합성 검증
 * - 금전적 수치는 외부에서 직접 변경 불가 (캡슐화)
 */
@Value
public class Money {

    Long moneyId;
    String membershipId;
    Long balance;

    /**
     * 잔액 충전
     * @param amount 충전 금액 (양수)
     * @return 충전된 새로운 Money 객체 (불변)
     */
    public Money increase(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다: " + amount);
        }

        return new Money(this.moneyId, this.membershipId, this.balance + amount);
    }

    /**
     * 잔액 차감
     * @param amount 차감 금액 (양수)
     * @return 차감된 새로운 Money 객체 (불변)
     * @throws InsufficientBalanceException 잔액 부족 시
     */
    public Money decrease(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("차감 금액은 0보다 커야 합니다: " + amount);
        }

        if (this.balance < amount) {
            throw new InsufficientBalanceException(
                String.format("잔액이 부족합니다. 현재 잔액: %d, 요청 금액: %d", this.balance, amount)
            );
        }

        return new Money(this.moneyId, this.membershipId, this.balance - amount);
    }

    /**
     * 잔액 부족 예외
     */
    public static class InsufficientBalanceException extends RuntimeException {
        public InsufficientBalanceException(String message) {
            super(message);
        }
    }
}
