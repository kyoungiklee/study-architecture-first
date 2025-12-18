package org.opennuri.study.architecture.money.adapter.out.persistence;

import org.opennuri.study.architecture.money.domain.Money;
import org.opennuri.study.architecture.money.domain.MoneyChangingHistory;
import org.springframework.stereotype.Component;

/**
 * Entity와 Domain 모델 간 변환을 담당하는 Mapper
 */
@Component
public class MoneyMapper {

    /**
     * MoneyEntity를 Money 도메인 모델로 변환
     */
    public Money toDomain(MoneyEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Money(
            entity.getMoneyId(),
            entity.getMembershipId(),
            entity.getBalance()
        );
    }

    /**
     * Money 도메인 모델을 MoneyEntity로 변환
     */
    public MoneyEntity toEntity(Money money) {
        if (money == null) {
            return null;
        }

        return new MoneyEntity(
            money.getMoneyId(),
            money.getMembershipId(),
            money.getBalance()
        );
    }

    /**
     * MoneyChangingHistoryEntity를 MoneyChangingHistory 도메인 모델로 변환
     */
    public MoneyChangingHistory toDomain(MoneyChangingHistoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return new MoneyChangingHistory(
            entity.getMoneyChangingHistoryId(),
            entity.getMembershipId(),
            MoneyChangingHistory.MoneyChangingType.fromCode(entity.getChangingType()),
            entity.getAmount(),
            entity.getCreatedAt()
        );
    }

    /**
     * MoneyChangingHistory 도메인 모델을 MoneyChangingHistoryEntity로 변환
     */
    public MoneyChangingHistoryEntity toEntity(MoneyChangingHistory history) {
        if (history == null) {
            return null;
        }

        return new MoneyChangingHistoryEntity(
            history.getMoneyChangingHistoryId(),
            history.getMembershipId(),
            history.getChangingType().getCode(),
            history.getAmount(),
            history.getCreatedAt()
        );
    }
}
