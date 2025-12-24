package org.opennuri.study.architecture.remittance.adapter.out.service.money;

import org.opennuri.study.architecture.remittance.application.port.out.CommitMoneyPort;
import org.opennuri.study.architecture.remittance.application.port.out.ReserveMoneyPort;
import org.opennuri.study.architecture.remittance.application.port.out.RollbackMoneyPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
public class MoneyServiceClientAdapter implements ReserveMoneyPort, CommitMoneyPort, RollbackMoneyPort {

    private final WebClient webClient;

    public MoneyServiceClientAdapter(
            @Value("${nexuspay.client.money.base-url}") String baseUrl,
            WebClient.Builder builder
    ) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public MoneyReservation reserveMoney(String membershipId, Long amount, String idempotencyKey) {
        // Mocking for scaffolding
        return new MoneyReservation(UUID.randomUUID().toString(), true);
    }

    @Override
    public boolean commitMoney(String reservationId) {
        // Mocking for scaffolding
        return true;
    }

    @Override
    public boolean rollbackMoney(String reservationId, String reason) {
        // Mocking for scaffolding
        return true;
    }
}
