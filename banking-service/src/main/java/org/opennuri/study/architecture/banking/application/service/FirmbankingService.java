package org.opennuri.study.architecture.banking.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.architecture.banking.application.port.in.RequestFirmbankingUseCase;
import org.opennuri.study.architecture.banking.application.port.in.command.RequestFirmbankingCommand;
import org.opennuri.study.architecture.banking.application.port.out.*;
import org.opennuri.study.architecture.banking.domain.Firmbanking;
import org.opennuri.study.architecture.banking.domain.FirmbankingHistory;
import org.opennuri.study.archtecture.common.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 펌뱅킹 서비스
 * - 펌뱅킹 요청 처리
 * - 외부 은행 연동
 * - 펌뱅킹 이력 관리
 */
@Slf4j
@UseCase
@RequiredArgsConstructor
@Transactional
public class FirmbankingService implements RequestFirmbankingUseCase {

    private final CommandFirmbankingPort commandFirmbankingPort;
    private final CommandFirmbankingHistoryPort commandFirmbankingHistoryPort;
    private final ExternalBankPort externalBankPort;
    private final MembershipPort membershipPort;

    @Override
    public Firmbanking requestFirmbanking(RequestFirmbankingCommand command) {
        log.info("펌뱅킹 요청 시작 - memberId: {}, amount: {}", command.getMemberId(), command.getAmount());

        // 1. 회원 검증
        MembershipPort.Status membershipStatus = membershipPort.getMembershipStatus(command.getMemberId());
        if (membershipStatus != MembershipPort.Status.ACTIVE) {
            throw new IllegalStateException("활성 상태의 회원만 펌뱅킹을 요청할 수 있습니다. 현재 상태: " + membershipStatus);
        }

        // 2. 펌뱅킹 도메인 객체 생성 (REQUESTED 상태)
        Firmbanking firmbanking = new Firmbanking(
            null,
            command.getMemberId(),
            command.getFromBankCode(),
            command.getFromBankAccount(),
            command.getToBankCode(),
            command.getToBankAccount(),
            command.getAmount(),
            Firmbanking.FirmbankingStatus.REQUESTED,
            LocalDateTime.now(),
            null
        );

        // 3. 금액 유효성 검증
        firmbanking.validateAmount();

        // 4. 펌뱅킹 요청 저장 (REQUESTED 상태)
        Firmbanking savedFirmbanking = commandFirmbankingPort.save(firmbanking);

        // 5. 이력 저장 (REQUESTED)
        saveHistory(savedFirmbanking.getId(), Firmbanking.FirmbankingStatus.REQUESTED, "펌뱅킹 요청 생성");

        // 6. 진행중 상태로 변경
        Firmbanking inProgressFirmbanking = savedFirmbanking.startProcessing();
        Firmbanking updatedFirmbanking = commandFirmbankingPort.save(inProgressFirmbanking);

        // 7. 이력 저장 (IN_PROGRESS)
        saveHistory(updatedFirmbanking.getId(), Firmbanking.FirmbankingStatus.IN_PROGRESS, "외부 은행 처리 시작");

        // 8. 외부 은행 이체 요청 (여기서 Resilience4j가 적용될 예정)
        try {
            String fullFromAccount = command.getFromBankCode() + ":" + command.getFromBankAccount();
            String fullToAccount = command.getToBankCode() + ":" + command.getToBankAccount();

            boolean transferSuccess = externalBankPort.transfer(
                fullFromAccount,
                fullToAccount,
                command.getAmount()
            );

            if (transferSuccess) {
                // 9-1. 성공: 완료 상태로 변경
                Firmbanking completedFirmbanking = updatedFirmbanking.complete();
                Firmbanking finalFirmbanking = commandFirmbankingPort.save(completedFirmbanking);

                saveHistory(finalFirmbanking.getId(), Firmbanking.FirmbankingStatus.COMPLETED, "외부 은행 이체 성공");

                log.info("펌뱅킹 완료 - firmbankingId: {}", finalFirmbanking.getId());
                return finalFirmbanking;
            } else {
                // 9-2. 실패: 실패 상태로 변경
                Firmbanking failedFirmbanking = updatedFirmbanking.fail();
                Firmbanking finalFirmbanking = commandFirmbankingPort.save(failedFirmbanking);

                saveHistory(finalFirmbanking.getId(), Firmbanking.FirmbankingStatus.FAILED, "외부 은행 이체 실패");

                log.warn("펌뱅킹 실패 - firmbankingId: {}", finalFirmbanking.getId());
                return finalFirmbanking;
            }
        } catch (Exception e) {
            // 10. 예외 발생: 실패 상태로 변경
            log.error("펌뱅킹 처리 중 예외 발생 - firmbankingId: {}", updatedFirmbanking.getId(), e);

            Firmbanking failedFirmbanking = updatedFirmbanking.fail();
            Firmbanking finalFirmbanking = commandFirmbankingPort.save(failedFirmbanking);

            saveHistory(finalFirmbanking.getId(), Firmbanking.FirmbankingStatus.FAILED,
                "외부 은행 통신 오류: " + e.getMessage());

            return finalFirmbanking;
        }
    }

    private void saveHistory(Long firmbankingId, Firmbanking.FirmbankingStatus status, String description) {
        FirmbankingHistory history = new FirmbankingHistory(
            null,
            firmbankingId,
            status,
            description,
            LocalDateTime.now()
        );
        commandFirmbankingHistoryPort.saveHistory(history);
    }
}
