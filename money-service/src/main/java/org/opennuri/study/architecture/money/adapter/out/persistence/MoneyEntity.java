package org.opennuri.study.architecture.money.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Money JPA Entity
 * 데이터베이스 저장을 위한 영속성 객체
 * setter를 제거하여 외부에서 직접 잔액 변경 방지
 */
@Entity
@Table(name = "money")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MoneyEntity {
    @Id
    @GeneratedValue
    private Long moneyId;

    private String membershipId;

    private Long balance;

    public MoneyEntity(String membershipId, Long balance) {
        this.membershipId = membershipId;
        this.balance = balance;
    }
}
