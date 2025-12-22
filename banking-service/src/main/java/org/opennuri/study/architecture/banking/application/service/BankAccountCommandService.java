package org.opennuri.study.architecture.banking.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.banking.application.port.in.RegisterBankAccountUseCase;
import org.opennuri.study.architecture.banking.application.port.in.command.CreateBankAccountCommand;
import org.opennuri.study.architecture.banking.application.port.in.command.UpdateBankAccountCommand;
import org.opennuri.study.architecture.banking.application.port.out.CommandBankAccountPort;
import org.opennuri.study.architecture.banking.domain.BankAccount;
import org.opennuri.study.archtecture.common.UseCase;

/**
 * 은행 계좌 관련 명령(생성, 수정, 삭제)을 처리하는 애플리케이션 서비스입니다.
 */
@UseCase
@RequiredArgsConstructor
public class BankAccountCommandService implements RegisterBankAccountUseCase {

    private final CommandBankAccountPort commandBankAccountPort;

    /**
     * 새로운 은행 계좌를 등록합니다.
     *
     * @param command 계좌 등록 정보
     * @return 등록된 계좌 도메인 모델
     */
    @Override
    public BankAccount registerBankAccount(CreateBankAccountCommand command) {
        BankAccount account = BankAccount.builder()
                .memberId(command.getMemberId())
                .bankCode(command.getBankCode())
                .bankAccountNo(command.getBankAccountNo())
                .valid(command.getValid())
                .build();
        return commandBankAccountPort.create(account);
    }

    /**
     * 기존 은행 계좌 정보를 수정합니다.
     *
     * @param command 계좌 수정 정보
     * @return 수정된 계좌 도메인 모델
     */
    public BankAccount update(UpdateBankAccountCommand command) {
        BankAccount account = BankAccount.builder()
                .id(command.getId())
                .memberId(command.getMemberId())
                .bankCode(command.getBankCode())
                .bankAccountNo(command.getBankAccountNo())
                .valid(command.getValid())
                .build();
        return commandBankAccountPort.update(account);
    }

    /**
     * 은행 계좌를 삭제합니다.
     *
     * @param id 삭제할 계좌 ID
     * @return 삭제 성공 여부
     */
    public boolean delete(Long id) {
        return commandBankAccountPort.deleteById(id);
    }
}
