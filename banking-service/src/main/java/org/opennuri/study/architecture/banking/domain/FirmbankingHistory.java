package org.opennuri.study.architecture.banking.domain;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * FirmbankingHistory 도메인 모델
 * 펌뱅킹 상태 변경 이력을 나타내는 순수 도메인 객체
 */
@Value
public class FirmbankingHistory {
    Long id;
    Long firmbankingId;
    Firmbanking.FirmbankingStatus status;
    String description;
    LocalDateTime createdAt;
}
