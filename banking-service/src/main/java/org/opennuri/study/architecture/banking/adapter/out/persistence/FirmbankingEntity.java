package org.opennuri.study.architecture.banking.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Firmbanking JPA Entity
 * 데이터베이스 저장을 위한 영속성 객체
 */
@Entity
@Table(name = "firmbanking")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmbankingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 20)
    private String fromBankCode;

    @Column(nullable = false, length = 50)
    private String fromBankAccount;

    @Column(nullable = false, length = 20)
    private String toBankCode;

    @Column(nullable = false, length = 50)
    private String toBankAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private FirmbankingStatusEntity status;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Column
    private LocalDateTime completedAt;

    public enum FirmbankingStatusEntity {
        REQUESTED,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}
