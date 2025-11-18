package org.opennuri.study.architecture.membership.application.port.in;

import lombok.Builder;
import lombok.Value;

/**
 * 복합 검색 요청 파라미터. 모든 항목은 선택값(null 허용)입니다.
 */
@Value
@Builder
public class SearchMembershipQuery {
    String name;      // 부분일치 검색
    String email;     // 부분일치 검색
    String address;   // 부분일치 검색
    Boolean isCorp;   // 정확일치 (null 이면 무시)
    Boolean isValid;  // 정확일치 (null 이면 무시)
}
