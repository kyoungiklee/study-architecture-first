package org.opennuri.study.architecture.money.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.money.application.port.in.GetMemberMoneyUseCase;
import org.opennuri.study.architecture.money.application.port.in.GetMoneyChangingHistoryUseCase;
import org.opennuri.study.architecture.money.application.port.in.RequestMoneyChangingUseCase;
import org.opennuri.study.architecture.money.application.port.out.LoadMoneyChangingHistoryPort;
import org.opennuri.study.architecture.money.application.port.out.LoadMoneyPort;
import org.opennuri.study.architecture.money.application.port.out.SaveMoneyChangingHistoryPort;
import org.opennuri.study.architecture.money.application.port.out.SaveMoneyPort;
import org.opennuri.study.architecture.money.domain.Money;
import org.opennuri.study.architecture.money.domain.MoneyChangingHistory;
import org.opennuri.study.archtecture.common.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 머니 충전/차감/조회 서비스
 * 도메인 모델을 사용하여 비즈니스 로직을 도메인 계층에 위임
 */
@UseCase
@RequiredArgsConstructor
@Transactional
public class MoneyChangingService implements RequestMoneyChangingUseCase, GetMemberMoneyUseCase, GetMoneyChangingHistoryUseCase {

    private final LoadMoneyPort loadMoneyPort;
    private final SaveMoneyPort saveMoneyPort;
    private final SaveMoneyChangingHistoryPort saveMoneyChangingHistoryPort;
    private final LoadMoneyChangingHistoryPort loadMoneyChangingHistoryPort;

    @Override
    public void increaseMoney(IncreaseMoneyRequest request) {
        // 1. 도메인 모델 로드
        Money money = loadMoneyPort.loadMoney(request.getTargetMembershipId());

        // 2. 도메인 로직 실행 (불변 객체이므로 새로운 인스턴스 반환)
        Money increasedMoney = money.increase(request.getAmount());

        // 3. 변경된 도메인 모델 저장
        saveMoneyPort.saveMoney(increasedMoney);

        // 4. 이력 저장
        MoneyChangingHistory history = new MoneyChangingHistory(
                null,
                request.getTargetMembershipId(),
                MoneyChangingHistory.MoneyChangingType.INCREASE,
                request.getAmount(),
                LocalDateTime.now()
        );
        saveMoneyChangingHistoryPort.saveMoneyChangingHistory(history);
    }

    @Override
    public void decreaseMoney(DecreaseMoneyRequest request) {
        // 1. 도메인 모델 로드
        Money money = loadMoneyPort.loadMoney(request.getTargetMembershipId());

        // 2. 도메인 로직 실행 (잔액 부족 시 InsufficientBalanceException 발생)
        Money decreasedMoney = money.decrease(request.getAmount());

        // 3. 변경된 도메인 모델 저장
        saveMoneyPort.saveMoney(decreasedMoney);

        // 4. 이력 저장
        MoneyChangingHistory history = new MoneyChangingHistory(
                null,
                request.getTargetMembershipId(),
                MoneyChangingHistory.MoneyChangingType.DECREASE,
                request.getAmount(),
                LocalDateTime.now()
        );
        saveMoneyChangingHistoryPort.saveMoneyChangingHistory(history);
    }

    @Override
    public Long getMemberMoney(String membershipId) {
        Money money = loadMoneyPort.loadMoney(membershipId);
        return money.getBalance();
    }

    @Override
    public List<MoneyChangingHistory> getHistory(String membershipId) {
        return loadMoneyChangingHistoryPort.loadMoneyChangingHistory(membershipId);
    }
}
