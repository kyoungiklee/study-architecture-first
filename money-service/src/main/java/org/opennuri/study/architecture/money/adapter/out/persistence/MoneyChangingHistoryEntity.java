package org.opennuri.study.architecture.money.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "money_changing_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoneyChangingHistoryEntity {
    @Id
    @GeneratedValue
    private Long moneyChangingHistoryId;

    private String membershipId;

    private int changingType; // 0: increase, 1: decrease

    private Long amount;

    private LocalDateTime createdAt;

    public MoneyChangingHistoryEntity(String membershipId, int changingType, Long amount, LocalDateTime createdAt) {
        this.membershipId = membershipId;
        this.changingType = changingType;
        this.amount = amount;
        this.createdAt = createdAt;
    }
}
