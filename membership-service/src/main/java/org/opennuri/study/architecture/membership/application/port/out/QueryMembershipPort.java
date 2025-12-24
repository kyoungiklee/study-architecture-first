package org.opennuri.study.architecture.membership.application.port.out;

import org.opennuri.study.architecture.membership.domain.Membership;

import java.util.List;
import java.util.Optional;

/**
 * 멤버십 조회를 위한 아웃바운드 포트입니다.
 * 영속성 계층(Persistence Adapter)에서 이 인터페이스를 구현합니다.
 */
public interface QueryMembershipPort {
    /**
     * ID로 멤버십을 조회합니다.
     *
     * @param membershipId 멤버십 ID
     * @return 멤버십 도메인 모델 (Optional)
     */
    Optional<Membership> findById(String membershipId);

    /**
     * 조건에 맞는 멤버십을 검색합니다.
     * null인 파라미터는 검색 조건에서 제외됩니다.
     *
     * @param name 이름
     * @param email 이메일
     * @param address 주소
     * @param isCorp 법인 여부
     * @param isValid 유효 상태
     * @return 검색된 멤버십 목록
     */
    List<Membership> search(String name, String email, String address, Boolean isCorp, Boolean isValid);
}
