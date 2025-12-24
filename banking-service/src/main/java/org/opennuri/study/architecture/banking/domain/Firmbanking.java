package org.opennuri.study.architecture.banking.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Firmbanking 도메인 모델 (Hexagonal 아키텍처의 도메인 계층)
 * 펌뱅킹 요청을 나타내는 순수 도메인 객체
 *
 * 책임:
 * - 펌뱅킹 요청 정보 관리
 * - 상태 전이 검증
 * - 금액 유효성 검증
 */
@Value
public class Firmbanking {
    Long id;
    Long memberId;
    String fromBankCode;
    String fromBankAccount;
    String toBankCode;
    String toBankAccount;
    BigDecimal amount;
    FirmbankingStatus status;
    LocalDateTime requestedAt;
    LocalDateTime completedAt;

    /**
     * 펌뱅킹 상태
     */
    public enum FirmbankingStatus {
        REQUESTED("요청됨"),
        IN_PROGRESS("진행중"),
        COMPLETED("완료"),
        FAILED("실패"),
        CANCELLED("취소됨");

        private final String description;

        FirmbankingStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 펌뱅킹 요청 완료 처리
     */
    public Firmbanking complete() {
        if (this.status != FirmbankingStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                String.format("진행중 상태에서만 완료할 수 있습니다. 현재 상태: %s", this.status)
            );
        }

        return new Firmbanking(
            this.id,
            this.memberId,
            this.fromBankCode,
            this.fromBankAccount,
            this.toBankCode,
            this.toBankAccount,
            this.amount,
            FirmbankingStatus.COMPLETED,
            this.requestedAt,
            LocalDateTime.now()
        );
    }

    /**
     * 펌뱅킹 요청 실패 처리
     */
    public Firmbanking fail() {
        if (this.status != FirmbankingStatus.REQUESTED && this.status != FirmbankingStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                String.format("요청됨 또는 진행중 상태에서만 실패 처리할 수 있습니다. 현재 상태: %s", this.status)
            );
        }

        return new Firmbanking(
            this.id,
            this.memberId,
            this.fromBankCode,
            this.fromBankAccount,
            this.toBankCode,
            this.toBankAccount,
            this.amount,
            FirmbankingStatus.FAILED,
            this.requestedAt,
            LocalDateTime.now()
        );
    }

    /**
     * 진행중 상태로 변경
     */
    public Firmbanking startProcessing() {
        if (this.status != FirmbankingStatus.REQUESTED) {
            throw new IllegalStateException(
                String.format("요청됨 상태에서만 진행중으로 변경할 수 있습니다. 현재 상태: %s", this.status)
            );
        }

        return new Firmbanking(
            this.id,
            this.memberId,
            this.fromBankCode,
            this.fromBankAccount,
            this.toBankCode,
            this.toBankAccount,
            this.amount,
            FirmbankingStatus.IN_PROGRESS,
            this.requestedAt,
            this.completedAt
        );
    }

    /**
     * 금액 유효성 검증
     */
    public void validateAmount() {
        if (this.amount == null || this.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("펌뱅킹 금액은 0보다 커야 합니다: " + this.amount);
        }
    }
}
