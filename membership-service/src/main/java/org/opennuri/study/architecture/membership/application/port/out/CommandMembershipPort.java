package org.opennuri.study.architecture.membership.application.port.out;

import org.opennuri.study.architecture.membership.domain.Membership;

/**
 * 멤버십 데이터 변경을 위한 아웃바운드 포트입니다.
 * 영속성 계층(Persistence Adapter)에서 이 인터페이스를 구현합니다.
 */
public interface CommandMembershipPort {
    /**
     * 멤버십을 생성합니다.
     *
     * @param membership 저장할 멤버십 정보
     * @return 저장된 멤버십 정보
     */
    Membership crateMembership(Membership membership);

    /**
     * 멤버십 정보를 업데이트합니다.
     *
     * @param membership 업데이트할 멤버십 정보
     * @return 업데이트된 멤버십 정보
     */
    Membership updateMembership(Membership membership);

    /**
     * 멤버십을 삭제합니다.
     *
     * @param membershipId 삭제할 멤버십 ID
     * @return 삭제 성공 여부
     */
    boolean deleteById(String membershipId);
}
