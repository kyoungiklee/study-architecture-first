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

/**
 * 머니 서비스의 영속성 계층을 담당하는 어댑터입니다.
 * 외부 포트(Port) 인터페이스를 구현하여 데이터베이스와의 상호작용을 처리합니다.
 */
@PersistanceAdapter
@RequiredArgsConstructor
public class MoneyPersistenceAdapter implements LoadMoneyPort, SaveMoneyPort, SaveMoneyChangingHistoryPort, LoadMoneyChangingHistoryPort {

    private final MoneyRepository moneyRepository;
    private final MoneyChangingHistoryRepository moneyChangingHistoryRepository;
    private final MoneyMapper moneyMapper;

    /**
     * 특정 멤버십의 머니 정보를 조회합니다. 존재하지 않을 경우 새로 생성하여 저장합니다.
     *
     * @param membershipId 멤버십 ID
     * @return 머니 도메인 모델
     */
    @Override
    public Money loadMoney(String membershipId) {
        MoneyEntity entity = moneyRepository.findByMembershipId(membershipId)
                .orElseGet(() -> {
                    MoneyEntity newEntity = new MoneyEntity(membershipId, 0L);
                    return moneyRepository.save(newEntity);
                });
        return moneyMapper.toDomain(entity);
    }

    /**
     * 머니 정보를 영속성 계층에 저장합니다.
     *
     * @param money 머니 도메인 모델
     */
    @Override
    public void saveMoney(Money money) {
        MoneyEntity entity = moneyMapper.toEntity(money);
        moneyRepository.save(entity);
    }

    /**
     * 머니 변동 이력을 영속성 계층에 저장합니다.
     *
     * @param history 머니 변동 이력 도메인 모델
     */
    @Override
    public void saveMoneyChangingHistory(MoneyChangingHistory history) {
        MoneyChangingHistoryEntity entity = moneyMapper.toEntity(history);
        moneyChangingHistoryRepository.save(entity);
    }

    /**
     * 특정 멤버십의 머니 변동 이력 목록을 조회합니다.
     *
     * @param membershipId 멤버십 ID
     * @return 머니 변동 이력 도메인 모델 목록
     */
    @Override
    public List<MoneyChangingHistory> loadMoneyChangingHistory(String membershipId) {
        List<MoneyChangingHistoryEntity> entities = moneyChangingHistoryRepository.findByMembershipId(membershipId);
        return entities.stream()
                .map(moneyMapper::toDomain)
                .collect(Collectors.toList());
    }
}
