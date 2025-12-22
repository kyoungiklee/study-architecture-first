package org.opennuri.study.architecture.payment.adapter.out.service.membership;

import org.opennuri.study.architecture.payment.application.port.out.LoadMemberPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
public class StubMemberAdapter implements LoadMemberPort {

    @Override
    public MemberStatus loadMember(Long memberId) {
        return MemberStatus.ACTIVE;
    }
}
