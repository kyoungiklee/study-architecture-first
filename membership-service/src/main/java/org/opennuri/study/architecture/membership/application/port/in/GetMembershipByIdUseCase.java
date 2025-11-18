package org.opennuri.study.architecture.membership.application.port.in;

import org.opennuri.study.architecture.membership.domain.Membership;

import java.util.Optional;

public interface GetMembershipByIdUseCase {
    Optional<Membership> getMembershipById(String membershipId);
}
