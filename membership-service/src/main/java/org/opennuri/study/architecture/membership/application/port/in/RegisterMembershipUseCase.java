package org.opennuri.study.architecture.membership.application.port.in;

import org.opennuri.study.architecture.membership.domain.Membership;
import org.opennuri.study.archtecture.common.UseCase;

@UseCase
public interface RegisterMembershipUseCase {

    public Membership registerMembership(RegisterMembershipCommand command);

}
