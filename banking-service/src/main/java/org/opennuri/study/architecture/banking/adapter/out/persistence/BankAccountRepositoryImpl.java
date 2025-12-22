package org.opennuri.study.architecture.banking.adapter.out.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static org.opennuri.study.architecture.banking.adapter.out.persistence.QBankAccountEntity.bankAccountEntity;

@RequiredArgsConstructor
public class BankAccountRepositoryImpl implements BankAccountRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<BankAccountEntity> search(Long memberId,
                                          String bankCode,
                                          String bankAccountNo,
                                          Boolean valid,
                                          LocalDateTime createdFrom,
                                          LocalDateTime createdTo) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);

        BooleanBuilder where = new BooleanBuilder();
        if (memberId != null) {
            where.and(bankAccountEntity.memberId.eq(memberId));
        }
        if (bankCode != null && !bankCode.isEmpty()) {
            where.and(bankAccountEntity.bankCode.equalsIgnoreCase(bankCode));
        }
        if (bankAccountNo != null && !bankAccountNo.isEmpty()) {
            where.and(bankAccountEntity.bankAccountNo.containsIgnoreCase(bankAccountNo));
        }
        if (valid != null) {
            where.and(bankAccountEntity.valid.eq(valid));
        }
        if (createdFrom != null) {
            where.and(bankAccountEntity.createdAt.goe(createdFrom));
        }
        if (createdTo != null) {
            where.and(bankAccountEntity.createdAt.loe(createdTo));
        }

        return query.selectFrom(bankAccountEntity)
                .where(where)
                .fetch();
    }
}
