package org.opennuri.study.architecture.banking.adapter.out.service.membership;

import org.opennuri.study.architecture.banking.application.port.out.MembershipPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
public class StubMembershipAdapter implements MembershipPort {
    @Override
    public Status getMembershipStatus(Long memberId) {
        // local 프로필용 더미 값
        return Status.ACTIVE;
    }
}
