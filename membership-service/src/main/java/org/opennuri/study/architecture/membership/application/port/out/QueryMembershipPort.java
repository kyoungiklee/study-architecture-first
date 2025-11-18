package org.opennuri.study.architecture.membership.application.port.out;

import org.opennuri.study.architecture.membership.domain.Membership;

import java.util.List;
import java.util.Optional;

public interface QueryMembershipPort {
    Optional<Membership> findById(String membershipId);

    /**
     * 복합 조건 검색. null 인 파라미터는 필터링에서 제외됩니다.
     */
    List<Membership> search(String name, String email, String address, Boolean isCorp, Boolean isValid);
}
