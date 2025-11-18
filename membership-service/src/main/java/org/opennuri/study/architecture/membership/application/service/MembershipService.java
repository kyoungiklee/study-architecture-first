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
 * CQRS 적용: 명령과 조회를 서로 다른 서비스로 분리
 */

@UseCase
@RequiredArgsConstructor
@Transactional
class MembershipCommandService implements RegisterMembershipUseCase, UpdateMembershipUseCase, DeleteMembershipUseCase {

    private final CommandMembershipPort commandPort;

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

    @Override
    public boolean deleteMembership(String membershipId) {
        return commandPort.deleteById(membershipId);
    }
}

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
class MembershipQueryService implements GetMembershipByIdUseCase, SearchMembershipUseCase {

    private final QueryMembershipPort queryPort;

    @Override
    public Optional<Membership> getMembershipById(String membershipId) {
        return queryPort.findById(membershipId);
    }

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
