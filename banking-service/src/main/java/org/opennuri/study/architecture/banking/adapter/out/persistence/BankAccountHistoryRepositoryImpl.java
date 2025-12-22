package org.opennuri.study.architecture.banking.adapter.out.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static org.opennuri.study.architecture.banking.adapter.out.persistence.QBankAccountHistoryEntity.bankAccountHistoryEntity;

@RequiredArgsConstructor
public class BankAccountHistoryRepositoryImpl implements BankAccountHistoryRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<BankAccountHistoryEntity> search(Long bankAccountId,
                                                 String action,
                                                 LocalDateTime from,
                                                 LocalDateTime to) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        BooleanBuilder where = new BooleanBuilder();
        if (bankAccountId != null) {
            where.and(bankAccountHistoryEntity.bankAccountId.eq(bankAccountId));
        }
        if (action != null && !action.isEmpty()) {
            where.and(bankAccountHistoryEntity.action.equalsIgnoreCase(action));
        }
        if (from != null) {
            where.and(bankAccountHistoryEntity.createdAt.goe(from));
        }
        if (to != null) {
            where.and(bankAccountHistoryEntity.createdAt.loe(to));
        }
        return query.selectFrom(bankAccountHistoryEntity)
                .where(where)
                .fetch();
    }
}
