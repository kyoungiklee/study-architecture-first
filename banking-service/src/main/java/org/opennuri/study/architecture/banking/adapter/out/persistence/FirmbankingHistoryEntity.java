package org.opennuri.study.architecture.banking.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * FirmbankingHistory JPA Entity
 * 펌뱅킹 상태 변경 이력 저장을 위한 영속성 객체
 */
@Entity
@Table(name = "firmbanking_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmbankingHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long firmbankingId;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private FirmbankingEntity.FirmbankingStatusEntity status;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
