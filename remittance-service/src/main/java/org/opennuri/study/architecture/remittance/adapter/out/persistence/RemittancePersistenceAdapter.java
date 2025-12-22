package org.opennuri.study.architecture.remittance.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.remittance.application.port.out.RemittanceRepositoryPort;
import org.opennuri.study.architecture.remittance.domain.model.Remittance;
import org.opennuri.study.archtecture.common.PersistanceAdapter;

@PersistanceAdapter
@RequiredArgsConstructor
public class RemittancePersistenceAdapter implements RemittanceRepositoryPort {
    private final SpringDataRemittanceRepository repository;

    @Override
    public Remittance save(Remittance remittance) {
        RemittanceJpaEntity entity = new RemittanceJpaEntity(
                remittance.getRemittanceId(),
                remittance.getFromMembershipId(),
                remittance.getToType(),
                remittance.getToTarget(),
                remittance.getAmount(),
                remittance.getReason(),
                remittance.getStatus(),
                remittance.getFailureCode(),
                remittance.getCreatedAt(),
                remittance.getUpdatedAt()
        );
        RemittanceJpaEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Remittance findByRemittanceId(String remittanceId) {
        return repository.findById(remittanceId)
                .map(this::mapToDomain)
                .orElse(null);
    }

    private Remittance mapToDomain(RemittanceJpaEntity entity) {
        return Remittance.createRemittance(
                entity.getRemittanceId(),
                entity.getFromMembershipId(),
                entity.getToType(),
                entity.getToTarget(),
                entity.getAmount(),
                entity.getReason(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
