package org.opennuri.study.architecture.membership.adapter.out.persistance;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static org.opennuri.study.architecture.membership.adapter.out.persistance.QMembershipEntity.membershipEntity;

/**
 * QueryDSL 기반 커스텀 구현 클래스. 네이밍 규칙: [RepositoryInterface]Impl
 */
@RequiredArgsConstructor
public class MembershipRepositoryImpl implements MembershipRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<MembershipEntity> searchUsingQuerydsl(String name,
                                                      String email,
                                                      String address,
                                                      Boolean isCorp,
                                                      Boolean isValid) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);

        BooleanBuilder where = new BooleanBuilder();
        if (name != null && !name.isEmpty()) {
            where.and(membershipEntity.name.containsIgnoreCase(name));
        }
        if (email != null && !email.isEmpty()) {
            where.and(membershipEntity.email.containsIgnoreCase(email));
        }
        if (address != null && !address.isEmpty()) {
            where.and(membershipEntity.address.containsIgnoreCase(address));
        }
        if (isCorp != null) {
            where.and(membershipEntity.isCorp.eq(isCorp));
        }
        if (isValid != null) {
            where.and(membershipEntity.isValid.eq(isValid));
        }

        return query.selectFrom(membershipEntity)
                .where(where)
                .fetch();
    }
}
