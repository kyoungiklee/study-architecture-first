package org.opennuri.study.architecture.membership.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.membership.application.port.in.*;
import org.opennuri.study.architecture.membership.application.port.out.CommandMembershipPort;
import org.opennuri.study.architecture.membership.application.port.out.QueryMembershipPort;
import org.opennuri.study.architecture.membership.domain.Membership;
import org.opennuri.study.archtecture.common.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 멤버십 관련 명령(등록, 수정, 삭제) 처리를 담당하는 서비스입니다.
 * CQRS 패턴에 따라 명령(Command) 책임을 수행합니다.
 */
@UseCase
@RequiredArgsConstructor
@Transactional
class MembershipCommandService implements RegisterMembershipUseCase, UpdateMembershipUseCase, DeleteMembershipUseCase {

    private final CommandMembershipPort commandPort;

    /**
     * 새로운 멤버십을 등록합니다.
     *
     * @param command 멤버십 등록 정보
     * @return 등록된 멤버십 도메인 모델
     */
    @Override
    public Membership registerMembership(RegisterMembershipCommand command) {
        // repository layer로 전달되는 값은 도메인 객체(Membership)이다.
        Membership membership = Membership.builder()
                .membershipId(null)
                .name(command.getName())
                .email(command.getEmail())
                .address(command.getAddress())
                .isCorp(command.isCorp())
                .isValid(command.isValid())
                .build();
        return commandPort.crateMembership(membership);
    }

    /**
     * 기존 멤버십 정보를 업데이트합니다.
     *
     * @param command 멤버십 수정 정보
     * @return 수정된 멤버십 도메인 모델
     */
    @Override
    public Membership updateMembership(UpdateMembershipCommand command) {
        Membership membership = Membership.builder()
                .membershipId(command.getMembershipId())
                .name(command.getName())
                .email(command.getEmail())
                .address(command.getAddress())
                .isCorp(command.isCorp())
                .isValid(true)
                .build();
        return commandPort.updateMembership(membership);
    }

    /**
     * 멤버십을 삭제(비활성화)합니다.
     *
     * @param membershipId 삭제할 멤버십 ID
     * @return 삭제 성공 여부
     */
    @Override
    public boolean deleteMembership(String membershipId) {
        return commandPort.deleteById(membershipId);
    }
}

/**
 * 멤버십 관련 조회 처리를 담당하는 서비스입니다.
 * CQRS 패턴에 따라 조회(Query) 책임을 수행합니다.
 */
@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
class MembershipQueryService implements GetMembershipByIdUseCase, SearchMembershipUseCase {

    private final QueryMembershipPort queryPort;

    /**
     * 멤버십 ID로 특정 멤버십을 조회합니다.
     *
     * @param membershipId 조회할 멤버십 ID
     * @return 멤버십 도메인 모델 (Optional)
     */
    @Override
    public Optional<Membership> getMembershipById(String membershipId) {
        return queryPort.findById(membershipId);
    }

    /**
     * 조건에 맞는 멤버십 목록을 검색합니다.
     *
     * @param query 검색 조건이 담긴 쿼리 객체
     * @return 검색된 멤버십 목록
     */
    @Override
    public List<Membership> searchMemberships(SearchMembershipQuery query) {
        return queryPort.search(
                query.getName(),
                query.getEmail(),
                query.getAddress(),
                query.getIsCorp(),
                query.getIsValid()
        );
    }
}
