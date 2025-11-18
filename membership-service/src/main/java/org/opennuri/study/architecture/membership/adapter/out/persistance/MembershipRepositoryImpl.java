package org.opennuri.study.architecture.membership.adapter.out.persistance;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static org.opennuri.study.architecture.membership.adapter.out.persistance.QMembershipJpaEntity.membershipJpaEntity;

/**
 * QueryDSL 기반 커스텀 구현 클래스. 네이밍 규칙: [RepositoryInterface]Impl
 */
@RequiredArgsConstructor
public class MembershipRepositoryImpl implements MembershipRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<MembershipJpaEntity> searchUsingQuerydsl(String name,
                                                         String email,
                                                         String address,
                                                         Boolean isCorp,
                                                         Boolean isValid) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);

        BooleanBuilder where = new BooleanBuilder();
        if (name != null && !name.isEmpty()) {
            where.and(membershipJpaEntity.name.containsIgnoreCase(name));
        }
        if (email != null && !email.isEmpty()) {
            where.and(membershipJpaEntity.email.containsIgnoreCase(email));
        }
        if (address != null && !address.isEmpty()) {
            where.and(membershipJpaEntity.address.containsIgnoreCase(address));
        }
        if (isCorp != null) {
            where.and(membershipJpaEntity.isCorp.eq(isCorp));
        }
        if (isValid != null) {
            where.and(membershipJpaEntity.isValid.eq(isValid));
        }

        return query.selectFrom(membershipJpaEntity)
                .where(where)
                .fetch();
    }
}
