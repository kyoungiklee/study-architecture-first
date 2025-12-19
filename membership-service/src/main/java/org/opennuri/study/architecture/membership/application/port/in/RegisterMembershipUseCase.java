package org.opennuri.study.architecture.membership.application.port.in;

import org.opennuri.study.architecture.membership.domain.Membership;
import org.opennuri.study.archtecture.common.UseCase;

/**
 * 멤버십 등록을 위한 인바운드 포트(인터페이스)입니다.
 * 외부 영역(예: Web Adapter)에서 멤버십을 등록하고자 할 때 이 인터페이스를 호출합니다.
 */
@UseCase
public interface RegisterMembershipUseCase {

    /**
     * 새로운 멤버십을 등록합니다.
     *
     * @param command 멤버십 등록에 필요한 정보가 담긴 커맨드 객체
     * @return 등록된 멤버십의 도메인 모델
     */
    public Membership registerMembership(RegisterMembershipCommand command);

}
