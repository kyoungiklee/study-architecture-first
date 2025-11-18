package org.opennuri.study.architecture.membership.application.port.out;

import org.opennuri.study.architecture.membership.domain.Membership;

public interface CommandMembershipPort {
    Membership crateMembership(Membership membership);
    Membership updateMembership(Membership membership);
    boolean deleteById(String membershipId);
}
