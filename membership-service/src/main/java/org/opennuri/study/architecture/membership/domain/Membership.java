package org.opennuri.study.architecture.membership.domain;

import lombok.*;

/**
 * Membership 도메인 모델 (Hexagonal 아키텍처의 도메인 계층)
 * 비즈니스 의미만을 담는 순수 도메인 객체로서,
 * 프레임워크나 영속성(JPA 등) 관련 어노테이션을 포함하지 않습니다.
 */

@Value
@Builder
public class Membership {

    /**
     * 개별 멤버(개인/법인)의 유니크한 아이디
     */
    String membershipId;

    /**
     * 개별 멤버(개인/법인)의 이름
     */
    String name;

    /**
     * 개별 멤버(개인/법인)의 이메일 주소
     */
    String email;

    /**
     * 개별 멤버(개인/법인)의 주소
     */
    String address;

    /**
     * 개별 멤버(개인/법인)의 현재 유효한 상태 여부
     */
    boolean isValid;

    /**
     * 개별 멤버가 법인 고객인지 여부
     */
    boolean isCorp;

}
