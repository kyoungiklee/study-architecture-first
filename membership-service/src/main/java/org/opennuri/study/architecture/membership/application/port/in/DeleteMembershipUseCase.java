package org.opennuri.study.architecture.membership.application.port.in;

import org.opennuri.study.archtecture.common.UseCase;

@UseCase
public interface DeleteMembershipUseCase {
    /**
     * 멤버십 삭제
     * @param membershipId 식별자
     * @return 삭제 성공 여부 (존재하지 않으면 false)
     */
    boolean deleteMembership(String membershipId);
}
