package org.opennuri.study.architecture.remittance.application.port.out;

public interface LoadMembershipPort {
    MembershipStatus loadMembershipStatus(String membershipId);
    
    record MembershipStatus(String membershipId, boolean isValid) {}
}
