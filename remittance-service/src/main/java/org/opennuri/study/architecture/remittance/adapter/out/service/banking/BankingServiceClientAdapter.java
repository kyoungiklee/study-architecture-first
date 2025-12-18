package org.opennuri.study.architecture.remittance.adapter.out.service.banking;

import org.opennuri.study.architecture.remittance.application.port.out.RequestFirmbankingPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BankingServiceClientAdapter implements RequestFirmbankingPort {

    private final WebClient webClient;

    public BankingServiceClientAdapter(
            @Value("${nexuspay.client.banking.base-url}") String baseUrl,
            WebClient.Builder builder
    ) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public FirmbankingResult requestFirmbanking(FirmbankingCommand command) {
        // Mocking for scaffolding
        return new FirmbankingResult(true);
    }
}
