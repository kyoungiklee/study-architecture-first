package org.opennuri.study.architecture.membership.application.port.in;

import org.opennuri.study.architecture.membership.domain.Membership;
import org.opennuri.study.archtecture.common.UseCase;

import java.util.List;

/**
 * 멤버십 검색을 위한 인바운드 포트입니다.
 */
@UseCase
public interface SearchMembershipUseCase {
    /**
     * 조건에 맞는 멤버십 목록을 검색합니다.
     *
     * @param query 검색 조건이 담긴 쿼리 객체
     * @return 검색된 멤버십 도메인 모델 리스트
     */
    List<Membership> searchMemberships(SearchMembershipQuery query);
}
