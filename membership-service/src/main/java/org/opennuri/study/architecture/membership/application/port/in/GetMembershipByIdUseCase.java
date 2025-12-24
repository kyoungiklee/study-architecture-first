package org.opennuri.study.architecture.membership.application.port.in;

import org.opennuri.study.architecture.membership.domain.Membership;
import org.opennuri.study.archtecture.common.UseCase;

import java.util.Optional;

/**
 * 멤버십 단건 조회를 위한 인바운드 포트입니다.
 */
@UseCase
public interface GetMembershipByIdUseCase {
    /**
     * ID로 멤버십 정보를 조회합니다.
     *
     * @param membershipId 조회할 멤버십 ID
     * @return 멤버십 도메인 모델 (Optional)
     */
    Optional<Membership> getMembershipById(String membershipId);
}
