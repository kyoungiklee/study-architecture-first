package org.opennuri.study.architecture.payment.application.port.out;

public interface LoadMemberPort {
    MemberStatus loadMember(Long memberId);

    enum MemberStatus {
        ACTIVE,
        INACTIVE
    }
}
