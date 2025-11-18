package org.opennuri.study.architecture.membership.application.port.in;

import org.opennuri.study.architecture.membership.domain.Membership;
import org.opennuri.study.archtecture.common.UseCase;

import java.util.List;

@UseCase
public interface SearchMembershipUseCase {
    List<Membership> searchMemberships(SearchMembershipQuery query);
}
