package org.opennuri.study.architecture.money.domain;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * MoneyChangingHistory 도메인 모델
 * 잔액 변동 이력을 나타내는 순수 도메인 객체
 */
@Value
public class MoneyChangingHistory {

    Long moneyChangingHistoryId;
    String membershipId;
    MoneyChangingType changingType;
    Long amount;
    LocalDateTime createdAt;

    /**
     * 잔액 변동 유형
     */
    public enum MoneyChangingType {
        INCREASE(0, "충전"),
        DECREASE(1, "차감");

        private final int code;
        private final String description;

        MoneyChangingType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static MoneyChangingType fromCode(int code) {
            for (MoneyChangingType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown MoneyChangingType code: " + code);
        }
    }
}
