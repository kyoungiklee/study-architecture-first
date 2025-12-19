package org.opennuri.study.architecture.banking.application.port.in;

import org.opennuri.study.architecture.banking.application.port.in.command.CreateBankAccountCommand;
import org.opennuri.study.architecture.banking.domain.BankAccount;

/**
 * 은행 계좌 등록을 위한 인바운드 포트(유스케이스) 인터페이스입니다.
 */
public interface RegisterBankAccountUseCase {
    /**
     * 새로운 은행 계좌를 등록합니다.
     *
     * @param command 계좌 등록 정보가 담긴 커맨드 객체
     * @return 등록된 계좌의 도메인 모델
     */
    BankAccount registerBankAccount(CreateBankAccountCommand command);
}
