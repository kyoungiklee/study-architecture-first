package org.opennuri.study.architecture.banking.application.port.out;

public interface MembershipPort {
    enum Status { ACTIVE, INACTIVE, BLOCKED }

    Status getMembershipStatus(Long memberId);
}
