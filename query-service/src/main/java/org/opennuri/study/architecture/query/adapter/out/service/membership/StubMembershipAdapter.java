package org.opennuri.study.architecture.query.adapter.out.service.membership;

import org.opennuri.study.architecture.query.application.port.out.GetMembershipPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Profile("local")
@Component
public class StubMembershipAdapter implements GetMembershipPort {
    @Override
    public Map<String, Object> getMembership(String memberId) {
        Map<String, Object> mock = new HashMap<>();
        mock.put("membershipId", memberId);
        mock.put("name", "John Doe");
        mock.put("email", "john.doe@example.com");
        return mock;
    }
}
