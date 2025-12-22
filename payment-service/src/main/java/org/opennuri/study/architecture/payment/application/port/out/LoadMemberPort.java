package org.opennuri.study.architecture.payment.application.port.out;

/**
 * 멤버 정보를 조회하기 위한 출력 포트 인터페이스
 */
public interface LoadMemberPort {
    /**
     * 멤버의 현재 상태를 조회합니다.
     *
     * @param memberId 멤버 ID
     * @return 멤버 상태 (ACTIVE, INACTIVE 등)
     */
    MemberStatus loadMember(Long memberId);

    /**
     * 멤버 상태 열거형
     */
    enum MemberStatus {
        /**
         * 활성 상태
         */
        ACTIVE,
        /**
         * 비활성 상태
         */
        INACTIVE
    }
}
