package org.opennuri.study.architecture.query.application.port.out;

import java.util.Map;

public interface GetMembershipPort {
    Map<String, Object> getMembership(String memberId);
}
