package org.opennuri.study.architecture.remittance.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.remittance.adapter.in.web.dto.RemitResponse;
import org.opennuri.study.architecture.remittance.application.port.in.RequestRemittanceUseCase;
import org.opennuri.study.architecture.remittance.application.port.out.*;
import org.opennuri.study.architecture.remittance.domain.model.Remittance;
import org.opennuri.study.architecture.remittance.domain.model.RemittanceStatus;
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
    private final RemittanceRepositoryPort remittanceRepositoryPort;

    @Override
    public RemitResponse requestRemittance(RemittanceCommand command) {

        // 0. Remittance 생성 및 저장 (REQUESTED)
        Remittance remittance = Remittance.createRemittance(
                null,
                command.getFromMembershipId(),
                command.getToType(),
                command.getToTarget(),
                command.getAmount(),
                command.getReason(),
                RemittanceStatus.REQUESTED,
                System.currentTimeMillis()
        );
        remittance = remittanceRepositoryPort.save(remittance);

        try {
            // 1. Membership 검증
            LoadMembershipPort.MembershipStatus fromStatus = loadMembershipPort.loadMembershipStatus(command.getFromMembershipId());
            if (!fromStatus.isValid()) {
                remittance.updateStatus(RemittanceStatus.FAILED, "INVALID_MEMBERSHIP");
                remittanceRepositoryPort.save(remittance);
                return RemitResponse.builder()
                        .remittanceId(remittance.getRemittanceId())
                        .status(remittance.getStatus().name())
                        .build();
            }

            // PROCESSING 상태로 전이
            remittance.updateStatus(RemittanceStatus.PROCESSING, null);
            remittanceRepositoryPort.save(remittance);

            // 2. Money 예약
            ReserveMoneyPort.MoneyReservation reservation = reserveMoneyPort.reserveMoney(
                    command.getFromMembershipId(),
                    command.getAmount(),
                    remittance.getRemittanceId() // remittanceId를 idempotencyKey로 사용
            );

            if (!reservation.isSuccess()) {
                remittance.updateStatus(RemittanceStatus.FAILED, "MONEY_RESERVATION_FAILED");
                remittanceRepositoryPort.save(remittance);
                return RemitResponse.builder()
                        .remittanceId(remittance.getRemittanceId())
                        .status(remittance.getStatus().name())
                        .build();
            }

            String reservationId = reservation.reservationId();

            if (command.getToType() == RemittanceType.MEMBERSHIP) {
                // 회원간 송금 (INTERNAL)
                boolean success = commitMoneyPort.commitMoney(reservationId);
                if (success) {
                    remittance.updateStatus(RemittanceStatus.SUCCESS, null);
                } else {
                    remittance.updateStatus(RemittanceStatus.FAILED, "MONEY_COMMIT_FAILED");
                    rollbackMoneyPort.rollbackMoney(reservationId, "Money commit failed");
                }
            } else if (command.getToType() == RemittanceType.BANK_ACCOUNT) {
                // 외부 계좌 송금 (EXTERNAL)
                // command.getToTarget() 형식이 "bankCode:accountNumber" 라고 가정하거나 별도 처리 필요
                // 여기서는 단순화를 위해 toTarget을 그대로 사용하거나 파싱
                String[] parts = command.getToTarget().split(":");
                String bankCode = parts.length > 0 ? parts[0] : "";
                String accountNumber = parts.length > 1 ? parts[1] : "";

                RequestFirmbankingPort.FirmbankingResult result = requestFirmbankingPort.requestFirmbanking(
                        RequestFirmbankingPort.FirmbankingCommand.builder()
                                .toBankCode(bankCode)
                                .toBankAccountNumber(accountNumber)
                                .amount(command.getAmount())
                                .build()
                );
                
                if (result.isSuccess()) {
                    boolean success = commitMoneyPort.commitMoney(reservationId);
                    if (success) {
                        remittance.updateStatus(RemittanceStatus.SUCCESS, null);
                    } else {
                        // 펌뱅킹은 성공했는데 머니 확정이 실패한 경우 - 보상 트랜잭션 등 복잡한 상황 발생 가능
                        // MVP 수준에서는 FAILED 처리
                        remittance.updateStatus(RemittanceStatus.FAILED, "MONEY_COMMIT_FAILED_AFTER_BANKING");
                    }
                } else {
                    remittance.updateStatus(RemittanceStatus.FAILED, "BANKING_TRANSFER_FAILED");
                    rollbackMoneyPort.rollbackMoney(reservationId, "Firmbanking failed");
                }
            }

            remittanceRepositoryPort.save(remittance);

        } catch (Exception e) {
            remittance.updateStatus(RemittanceStatus.FAILED, "UNEXPECTED_ERROR: " + e.getMessage());
            remittanceRepositoryPort.save(remittance);
            // 실제 환경에서는 예약 ID가 있다면 롤백 시도
            throw e;
        }

        return RemitResponse.builder()
                .remittanceId(remittance.getRemittanceId())
                .status(remittance.getStatus().name())
                .build();
    }
}
