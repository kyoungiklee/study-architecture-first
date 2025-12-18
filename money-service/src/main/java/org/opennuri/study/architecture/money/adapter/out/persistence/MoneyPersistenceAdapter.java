package org.opennuri.study.architecture.money.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.money.application.port.out.LoadMoneyChangingHistoryPort;
import org.opennuri.study.architecture.money.application.port.out.LoadMoneyPort;
import org.opennuri.study.architecture.money.application.port.out.SaveMoneyChangingHistoryPort;
import org.opennuri.study.architecture.money.application.port.out.SaveMoneyPort;
import org.opennuri.study.architecture.money.domain.Money;
import org.opennuri.study.architecture.money.domain.MoneyChangingHistory;
import org.opennuri.study.archtecture.common.PersistanceAdapter;

import java.util.List;
import java.util.stream.Collectors;

@PersistanceAdapter
@RequiredArgsConstructor
public class MoneyPersistenceAdapter implements LoadMoneyPort, SaveMoneyPort, SaveMoneyChangingHistoryPort, LoadMoneyChangingHistoryPort {

    private final MoneyRepository moneyRepository;
    private final MoneyChangingHistoryRepository moneyChangingHistoryRepository;
    private final MoneyMapper moneyMapper;

    @Override
    public Money loadMoney(String membershipId) {
        MoneyEntity entity = moneyRepository.findByMembershipId(membershipId)
                .orElseGet(() -> {
                    MoneyEntity newEntity = new MoneyEntity(membershipId, 0L);
                    return moneyRepository.save(newEntity);
                });
        return moneyMapper.toDomain(entity);
    }

    @Override
    public void saveMoney(Money money) {
        MoneyEntity entity = moneyMapper.toEntity(money);
        moneyRepository.save(entity);
    }

    @Override
    public void saveMoneyChangingHistory(MoneyChangingHistory history) {
        MoneyChangingHistoryEntity entity = moneyMapper.toEntity(history);
        moneyChangingHistoryRepository.save(entity);
    }

    @Override
    public List<MoneyChangingHistory> loadMoneyChangingHistory(String membershipId) {
        List<MoneyChangingHistoryEntity> entities = moneyChangingHistoryRepository.findByMembershipId(membershipId);
        return entities.stream()
                .map(moneyMapper::toDomain)
                .collect(Collectors.toList());
    }
}
