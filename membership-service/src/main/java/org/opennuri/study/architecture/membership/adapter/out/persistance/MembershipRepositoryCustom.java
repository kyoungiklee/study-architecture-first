package org.opennuri.study.architecture.membership.adapter.out.persistance;

import java.util.List;

/**
 * QueryDSL 기반 커스텀 조회 인터페이스
 */
public interface MembershipRepositoryCustom {

    /**
     * QueryDSL 기반 동적 검색.
     * - 문자열(name/email/address): containsIgnoreCase
     * - 불리언(isCorp/isValid): 정확 일치
     * - null 파라미터는 필터에서 제외
     */
    List<MembershipEntity> searchUsingQuerydsl(String name,
                                               String email,
                                               String address,
                                               Boolean isCorp,
                                               Boolean isValid);
}
