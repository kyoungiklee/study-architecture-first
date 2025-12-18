package org.opennuri.study.architecture.remittance.application.port.out;

import lombok.Builder;
import lombok.Data;

public interface RequestFirmbankingPort {
    FirmbankingResult requestFirmbanking(FirmbankingCommand command);

    @Data
    @Builder
    class FirmbankingCommand {
        private String fromBankCode;
        private String fromBankAccountNumber;
        private String toBankCode;
        private String toBankAccountNumber;
        private Long amount;
    }

    record FirmbankingResult(boolean isSuccess) {}
}
