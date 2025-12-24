package org.opennuri.study.architecture.membership.application.port.in;

import org.opennuri.study.architecture.membership.domain.Membership;
import org.opennuri.study.archtecture.common.UseCase;

/**
 * 멤버십 정보 수정을 위한 인바운드 포트입니다.
 */
@UseCase
public interface UpdateMembershipUseCase {
    /**
     * 기존 멤버십 정보를 업데이트합니다.
     *
     * @param command 멤버십 수정 정보가 담긴 커맨드 객체
     * @return 업데이트된 멤버십 도메인 모델
     */
    Membership updateMembership(UpdateMembershipCommand command);
}
