package org.opennuri.study.architecture.banking.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.banking.application.port.out.CommandFirmbankingHistoryPort;
import org.opennuri.study.architecture.banking.application.port.out.CommandFirmbankingPort;
import org.opennuri.study.architecture.banking.application.port.out.QueryFirmbankingHistoryPort;
import org.opennuri.study.architecture.banking.application.port.out.QueryFirmbankingPort;
import org.opennuri.study.architecture.banking.domain.Firmbanking;
import org.opennuri.study.architecture.banking.domain.FirmbankingHistory;
import org.opennuri.study.archtecture.common.PersistanceAdapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PersistanceAdapter
@RequiredArgsConstructor
public class FirmbankingPersistenceAdapter implements
    CommandFirmbankingPort,
    QueryFirmbankingPort,
    CommandFirmbankingHistoryPort,
    QueryFirmbankingHistoryPort {

    private final FirmbankingRepository firmbankingRepository;
    private final FirmbankingHistoryRepository firmbankingHistoryRepository;
    private final BankingMapper bankingMapper;

    @Override
    public Firmbanking save(Firmbanking firmbanking) {
        FirmbankingEntity entity = bankingMapper.toEntity(firmbanking);
        FirmbankingEntity saved = firmbankingRepository.save(entity);
        return bankingMapper.toDomain(saved);
    }

    @Override
    public Optional<Firmbanking> findById(Long id) {
        return firmbankingRepository.findById(id)
            .map(bankingMapper::toDomain);
    }

    @Override
    public List<Firmbanking> findByMemberId(Long memberId) {
        return firmbankingRepository.findByMemberId(memberId)
            .stream()
            .map(bankingMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void saveHistory(FirmbankingHistory history) {
        FirmbankingHistoryEntity entity = bankingMapper.toEntity(history);
        firmbankingHistoryRepository.save(entity);
    }

    @Override
    public List<FirmbankingHistory> findByFirmbankingId(Long firmbankingId) {
        return firmbankingHistoryRepository.findByFirmbankingIdOrderByCreatedAtDesc(firmbankingId)
            .stream()
            .map(bankingMapper::toDomain)
            .collect(Collectors.toList());
    }
}
