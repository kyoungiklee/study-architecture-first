package org.opennuri.study.architecture.money.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * MoneyChangingHistory 도메인 모델
 * 잔액 변동 이력을 나타내는 순수 도메인 객체
 */
@Value
@Schema(description = "머니 변동 이력 정보")
public class MoneyChangingHistory {

    @Schema(description = "머니 변동 이력 ID", example = "1")
    Long moneyChangingHistoryId;

    @Schema(description = "멤버십 ID", example = "1")
    String membershipId;

    @Schema(description = "변동 유형 (0: 충전, 1: 차감)", implementation = MoneyChangingType.class)
    MoneyChangingType changingType;

    @Schema(description = "변동 금액", example = "10000")
    Long amount;

    @Schema(description = "생성 일시")
    LocalDateTime createdAt;

    /**
     * 잔액 변동 유형
     */
    @Schema(description = "머니 변동 유형")
    public enum MoneyChangingType {
        INCREASE(0, "충전"),
        DECREASE(1, "차감");

        @Schema(description = "유형 코드")
        private final int code;
        @Schema(description = "유형 설명")
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
