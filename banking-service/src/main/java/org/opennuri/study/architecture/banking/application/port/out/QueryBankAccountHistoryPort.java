package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.BankAccountHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 은행 계좌 변경 이력 조회를 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface QueryBankAccountHistoryPort {
    /**
     * ID로 계좌 변경 이력을 조회합니다.
     *
     * @param id 이력 ID
     * @return 조회된 계좌 이력 도메인 모델 (Optional)
     */
    Optional<BankAccountHistory> findHistoryById(Long id);

    /**
     * 조건에 맞는 계좌 변경 이력 목록을 조회합니다.
     *
     * @param bankAccountId 계좌 ID
     * @param action 작업 구분
     * @param from 시작일 범위
     * @param to 종료일 범위
     * @return 검색된 이력 목록
     */
    List<BankAccountHistory> search(Long bankAccountId,
                                    String action,
                                    LocalDateTime from,
                                    LocalDateTime to);
}
