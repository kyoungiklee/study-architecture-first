package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.BankAccount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 은행 계좌 영속성 조회를 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface QueryBankAccountPort {
    /**
     * ID로 은행 계좌를 조회합니다.
     *
     * @param id 계좌 ID
     * @return 조회된 계좌 도메인 모델 (Optional)
     */
    Optional<BankAccount> findById(Long id);

    /**
     * 조건에 맞는 은행 계좌 리스트를 조회합니다.
     *
     * @param memberId 회원 ID
     * @param bankCode 은행 코드
     * @param bankAccountNo 계좌 번호
     * @param valid 유효 여부
     * @param createdFrom 생성일 시작 범위
     * @param createdTo 생성일 종료 범위
     * @return 검색된 계좌 목록
     */
    List<BankAccount> search(Long memberId,
                             String bankCode,
                             String bankAccountNo,
                             Boolean valid,
                             LocalDateTime createdFrom,
                             LocalDateTime createdTo);
}
