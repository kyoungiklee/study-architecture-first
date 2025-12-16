package org.opennuri.study.architecture.banking.application.port.in;

import org.opennuri.study.architecture.banking.application.port.in.command.CreateBankAccountCommand;
import org.opennuri.study.architecture.banking.domain.BankAccount;

/**
 * 은행 계좌 등록(생성) 유스케이스.
 * 기존 CreateBankAccountUseCase를 대체합니다.
 */
public interface RegisterBankAccountUseCase {
    BankAccount registerBankAccount(CreateBankAccountCommand command);
}
