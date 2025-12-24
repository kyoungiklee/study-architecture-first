package org.opennuri.study.architecture.payment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * 결제 도메인 엔티티
 * 결제 트랜잭션의 상태와 상세 정보를 관리합니다.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment {

    /**
     * 결제 고유 ID (PK)
     */
    @Getter private final Long paymentId;
    /**
     * 주문 ID (가맹점 관리 번호)
     */
    @Getter private final String orderId;
    /**
     * 사용자 고유 ID
     */
    @Getter private final Long userId;
    /**
     * 결제 금액
     */
    @Getter private final Long amount;
    /**
     * 결제 수단 (CARD, TRANSFER 등)
     */
    @Getter private final String payMethod;
    /**
     * 참조 ID (외부 연동 식별자)
     */
    @Getter private final String referenceId;
    /**
     * 결제 상태
     */
    @Getter private final PaymentStatus status;
    /**
     * 멱등성 키 (중복 요청 방지용)
     */
    @Getter private final String idempotencyKey;
    /**
     * 생성 시각
     */
    @Getter private final LocalDateTime createdAt;
    /**
     * 최종 수정 시각
     */
    @Getter private final LocalDateTime updatedAt;

    /**
     * 결제 엔티티 생성을 위한 팩토리 메서드 (ID 포함)
     */
    public static Payment withId(
            Long paymentId,
            String orderId,
            Long userId,
            Long amount,
            String payMethod,
            String referenceId,
            PaymentStatus status,
            String idempotencyKey,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new Payment(paymentId, orderId, userId, amount, payMethod, referenceId, status, idempotencyKey, createdAt, updatedAt);
    }

    /**
     * 결제 상태 열거형
     */
    public enum PaymentStatus {
        /**
         * 결제 객체 생성됨
         */
        CREATED,
        /**
         * 승인 요청 접수됨
         */
        REQUESTED,
        /**
         * 승인 완료됨 (결제 성공 의사결정 완료)
         */
        APPROVED,
        /**
         * 금전 이체 성공, 정산 반영 대기 중
         */
        SETTLEMENT_PENDING,
        /**
         * 정산 반영 완료 (최종 완료 상태)
         */
        SETTLED,
        /**
         * 승인 실패됨
         */
        FAILED,
        /**
         * 결제 취소 완료됨
         */
        CANCELED
    }

    /**
     * 결제 ID를 표현하는 값 객체 (Value Object)
     */
    @Value
    public static class PaymentId {
        /**
         * 결제 ID 값
         */
        Long value;
    }
}
