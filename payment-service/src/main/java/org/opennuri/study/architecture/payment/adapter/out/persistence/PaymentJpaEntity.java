package org.opennuri.study.architecture.payment.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 결제 정보를 위한 JPA 엔티티
 */
@Entity
@Table(name = "payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentJpaEntity {

    /**
     * 결제 고유 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    /**
     * 주문 ID
     */
    private String orderId;
    /**
     * 사용자 ID
     */
    private Long userId;
    /**
     * 결제 금액
     */
    private Long amount;
    /**
     * 결제 수단
     */
    private String payMethod;
    /**
     * 참조 ID (외부 연동용)
     */
    private String referenceId;
    /**
     * 결제 상태 (문자열 저장)
     */
    private String status;
    /**
     * 멱등성 키 (유니크 제약 조건)
     */
    @Column(unique = true)
    private String idempotencyKey;
    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;
    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;
}
