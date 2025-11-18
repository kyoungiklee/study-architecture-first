package org.opennuri.study.architecture.membership.application.port.in;

import org.opennuri.study.architecture.membership.domain.Membership;
import org.opennuri.study.archtecture.common.UseCase;

import java.util.Optional;

@UseCase
public interface GetMembershipByIdUseCase {
    Optional<Membership> getMembershipById(String membershipId);
}
