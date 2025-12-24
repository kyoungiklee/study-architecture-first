package org.opennuri.study.architecture.money.application.port.in;

/**
 * 멤버십의 머니 잔액을 조회하는 Use Case 인터페이스입니다.
 */
public interface GetMemberMoneyUseCase {
    /**
     * 특정 멤버십의 현재 머니 잔액을 조회합니다.
     *
     * @param membershipId 멤버십 ID
     * @return 머니 잔액
     */
    Long getMemberMoney(String membershipId);
}
