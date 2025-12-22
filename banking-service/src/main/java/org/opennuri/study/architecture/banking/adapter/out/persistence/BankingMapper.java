package org.opennuri.study.architecture.banking.adapter.out.persistence;

import org.opennuri.study.architecture.banking.domain.Firmbanking;
import org.opennuri.study.architecture.banking.domain.FirmbankingHistory;
import org.springframework.stereotype.Component;

/**
 * Entity와 Domain 모델 간 변환을 담당하는 Mapper
 */
@Component
public class BankingMapper {

    /**
     * FirmbankingEntity를 Firmbanking 도메인 모델로 변환
     */
    public Firmbanking toDomain(FirmbankingEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Firmbanking(
            entity.getId(),
            entity.getMemberId(),
            entity.getFromBankCode(),
            entity.getFromBankAccount(),
            entity.getToBankCode(),
            entity.getToBankAccount(),
            entity.getAmount(),
            toFirmbankingStatus(entity.getStatus()),
            entity.getRequestedAt(),
            entity.getCompletedAt()
        );
    }

    /**
     * Firmbanking 도메인 모델을 FirmbankingEntity로 변환
     */
    public FirmbankingEntity toEntity(Firmbanking firmbanking) {
        if (firmbanking == null) {
            return null;
        }

        return FirmbankingEntity.builder()
            .id(firmbanking.getId())
            .memberId(firmbanking.getMemberId())
            .fromBankCode(firmbanking.getFromBankCode())
            .fromBankAccount(firmbanking.getFromBankAccount())
            .toBankCode(firmbanking.getToBankCode())
            .toBankAccount(firmbanking.getToBankAccount())
            .amount(firmbanking.getAmount())
            .status(toFirmbankingStatusEntity(firmbanking.getStatus()))
            .requestedAt(firmbanking.getRequestedAt())
            .completedAt(firmbanking.getCompletedAt())
            .build();
    }

    /**
     * FirmbankingHistoryEntity를 FirmbankingHistory 도메인 모델로 변환
     */
    public FirmbankingHistory toDomain(FirmbankingHistoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return new FirmbankingHistory(
            entity.getId(),
            entity.getFirmbankingId(),
            toFirmbankingStatus(entity.getStatus()),
            entity.getDescription(),
            entity.getCreatedAt()
        );
    }

    /**
     * FirmbankingHistory 도메인 모델을 FirmbankingHistoryEntity로 변환
     */
    public FirmbankingHistoryEntity toEntity(FirmbankingHistory history) {
        if (history == null) {
            return null;
        }

        return FirmbankingHistoryEntity.builder()
            .id(history.getId())
            .firmbankingId(history.getFirmbankingId())
            .status(toFirmbankingStatusEntity(history.getStatus()))
            .description(history.getDescription())
            .createdAt(history.getCreatedAt())
            .build();
    }

    // Helper methods
    private Firmbanking.FirmbankingStatus toFirmbankingStatus(FirmbankingEntity.FirmbankingStatusEntity statusEntity) {
        if (statusEntity == null) {
            return null;
        }
        return Firmbanking.FirmbankingStatus.valueOf(statusEntity.name());
    }

    private FirmbankingEntity.FirmbankingStatusEntity toFirmbankingStatusEntity(Firmbanking.FirmbankingStatus status) {
        if (status == null) {
            return null;
        }
        return FirmbankingEntity.FirmbankingStatusEntity.valueOf(status.name());
    }
}
