package org.opennuri.study.architecture.remittance.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.remittance.adapter.in.web.dto.RemitResponse;
import org.opennuri.study.architecture.remittance.application.port.in.RequestRemittanceUseCase;
import org.opennuri.study.architecture.remittance.application.port.out.*;
import org.opennuri.study.architecture.remittance.domain.model.RemittanceType;
import org.opennuri.study.archtecture.common.UseCase;

@UseCase
@RequiredArgsConstructor
public class RequestRemittanceService implements RequestRemittanceUseCase {

    private final LoadMembershipPort loadMembershipPort;
    private final ReserveMoneyPort reserveMoneyPort;
    private final CommitMoneyPort commitMoneyPort;
    private final RollbackMoneyPort rollbackMoneyPort;
    private final RequestFirmbankingPort requestFirmbankingPort;

    @Override
    public RemitResponse requestRemittance(RemittanceCommand command) {
        // 1. Membership 검증
        LoadMembershipPort.MembershipStatus fromStatus = loadMembershipPort.loadMembershipStatus(command.getFromMembershipId());
        if (!fromStatus.isValid()) {
            throw new RuntimeException("Invalid from membership status");
        }

        // 2. Money 예약
        ReserveMoneyPort.MoneyReservation reservation = reserveMoneyPort.reserveMoney(
                command.getFromMembershipId(),
                command.getAmount(),
                command.getIdempotencyKey()
        );

        if (!reservation.isSuccess()) {
            return RemitResponse.builder()
                    .status("FAILED_MONEY_RESERVATION")
                    .build();
        }

        boolean success = false;
        String reservationId = reservation.reservationId();

        try {
            if (command.getToType() == RemittanceType.MEMBER) {
                // 회원간 송금
                // MVP에서는 단순화하여 from 차감 확정 후 to 증액 (이 부분은 요구사항에 따라 달라질 수 있음)
                // 여기서는 예약을 Commit 하는 것으로 처리
                success = commitMoneyPort.commitMoney(reservationId);
            } else if (command.getToType() == RemittanceType.BANK) {
                // 외부 계좌 송금
                RequestFirmbankingPort.FirmbankingResult result = requestFirmbankingPort.requestFirmbanking(
                        RequestFirmbankingPort.FirmbankingCommand.builder()
                                .toBankCode(command.getToBankCode())
                                .toBankAccountNumber(command.getToBankAccountNumber())
                                .amount(command.getAmount())
                                .build()
                );
                
                if (result.isSuccess()) {
                    success = commitMoneyPort.commitMoney(reservationId);
                } else {
                    rollbackMoneyPort.rollbackMoney(reservationId, "Firmbanking failed");
                }
            }
        } catch (Exception e) {
            rollbackMoneyPort.rollbackMoney(reservationId, e.getMessage());
            throw e;
        }

        return RemitResponse.builder()
                .remittanceId("remittance-id") // 임시 ID
                .status(success ? "COMPLETED" : "FAILED")
                .build();
    }
}
