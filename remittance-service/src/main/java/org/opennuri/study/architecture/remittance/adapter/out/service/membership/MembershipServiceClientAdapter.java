package org.opennuri.study.architecture.remittance.adapter.out.service.membership;

import org.opennuri.study.architecture.remittance.application.port.out.LoadMembershipPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MembershipServiceClientAdapter implements LoadMembershipPort {

    private final WebClient webClient;

    public MembershipServiceClientAdapter(
            @Value("${nexuspay.client.membership.base-url}") String baseUrl,
            WebClient.Builder builder
    ) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public MembershipStatus loadMembershipStatus(String membershipId) {
        // 실제 API 호출 로직 (MVP 단계에서는 stub 또는 실제 호출)
        // return webClient.get()
        //         .uri("/memberships/{id}", membershipId)
        //         .retrieve()
        //         .bodyToMono(MembershipStatusResponse.class)
        //         .map(res -> new MembershipStatus(res.getMembershipId(), res.isValid()))
        //         .block();
        
        // Mocking for scaffolding
        return new MembershipStatus(membershipId, true);
    }
}
