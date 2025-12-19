package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.BankAccount;

/**
 * 은행 계좌 영속성 수정을 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface CommandBankAccountPort {
    /**
     * 새로운 은행 계좌를 영속화합니다.
     *
     * @param account 저장할 계좌 도메인 모델
     * @return 저장된 계좌 도메인 모델 (ID 포함)
     */
    BankAccount create(BankAccount account);

    /**
     * 기존 은행 계좌 정보를 업데이트합니다.
     *
     * @param account 수정할 정보가 담긴 도메인 모델
     * @return 업데이트된 도메인 모델
     */
    BankAccount update(BankAccount account);

    /**
     * ID로 은행 계좌를 삭제합니다.
     *
     * @param id 삭제할 계좌 ID
     * @return 삭제 성공 시 true, 존재하지 않으면 false
     */
    boolean deleteById(Long id);
}
