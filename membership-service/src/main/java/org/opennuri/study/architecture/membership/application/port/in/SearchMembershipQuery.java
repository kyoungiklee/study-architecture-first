package org.opennuri.study.architecture.membership.application.port.in;

import lombok.Builder;
import lombok.Value;

/**
 * 멤버십 검색 조건을 담는 쿼리 객체입니다.
 * 모든 항목은 선택 사항(null 허용)이며, null인 항목은 필터링에서 제외됩니다.
 */
@Value
@Builder
public class SearchMembershipQuery {
    /**
     * 이름 (부분 일치 검색)
     */
    String name;

    /**
     * 이메일 (부분 일치 검색)
     */
    String email;

    /**
     * 주소 (부분 일치 검색)
     */
    String address;

    /**
     * 법인 여부 (정확 일치)
     */
    Boolean isCorp;

    /**
     * 유효 상태 (정확 일치)
     */
    Boolean isValid;
}
