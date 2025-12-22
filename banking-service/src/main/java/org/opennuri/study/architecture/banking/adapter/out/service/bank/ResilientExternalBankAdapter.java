package org.opennuri.study.architecture.banking.adapter.out.service.bank;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.architecture.banking.application.port.out.ExternalBankPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 외부 은행 연동 어댑터 (Resilience4j 적용)
 * - Circuit Breaker: 장애 발생 시 빠른 실패
 * - Retry: 일시적 장애 시 재시도
 * - TimeLimiter: 타임아웃 설정
 */
@Slf4j
@Profile("!local")
@Component
public class ResilientExternalBankAdapter implements ExternalBankPort {

    private static final String EXTERNAL_BANK = "externalBank";

    @Override
    @CircuitBreaker(name = EXTERNAL_BANK, fallbackMethod = "transferFallback")
    @Retry(name = EXTERNAL_BANK)
    // @TimeLimiter(name = EXTERNAL_BANK) // TimeLimiter는 리턴 타입이 Future/CompletionStage일 때 주로 사용됨. 동기 방식에서는 동작하지 않거나 경고를 유발할 수 있음.
    public boolean transfer(String fromBankAccount, String toBankAccount, BigDecimal amount) {
        log.info("외부 은행 이체 요청 - from: {}, to: {}, amount: {}", fromBankAccount, toBankAccount, amount);

        try {
            // 실제 외부 은행 API 호출 로직
            // TODO: RestTemplate, WebClient 등을 사용하여 실제 외부 은행 API 호출

            // 임시 구현: 50% 확률로 성공
            boolean success = Math.random() > 0.3;

            if (!success) {
                log.warn("외부 은행 이체 실패 - from: {}, to: {}", fromBankAccount, toBankAccount);
                throw new ExternalBankTransferException("외부 은행 이체 실패");
            }

            log.info("외부 은행 이체 성공 - from: {}, to: {}", fromBankAccount, toBankAccount);
            return true;

        } catch (Exception e) {
            log.error("외부 은행 이체 중 예외 발생", e);
            throw new ExternalBankTransferException("외부 은행 통신 오류", e);
        }
    }

    /**
     * 폴백 메서드: Circuit Breaker가 열려있거나 재시도 실패 시 호출
     */
    public boolean transferFallback(String fromBankAccount, String toBankAccount, BigDecimal amount, Exception e) {
        log.error("외부 은행 이체 폴백 실행 - from: {}, to: {}, error: {}",
            fromBankAccount, toBankAccount, e.getMessage());

        // 폴백 전략:
        // 1. 로그 기록
        // 2. 별도 큐에 저장하여 나중에 재처리
        // 3. false 반환하여 실패 표시

        return false;
    }

    /**
     * 외부 은행 이체 예외
     */
    public static class ExternalBankTransferException extends RuntimeException {
        public ExternalBankTransferException(String message) {
            super(message);
        }

        public ExternalBankTransferException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
