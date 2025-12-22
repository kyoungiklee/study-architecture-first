package org.opennuri.study.architecture.membership.adapter.out.persistance;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 멤버십 정보를 데이터베이스에 저장하기 위한 JPA 엔티티 클래스입니다.
 */
@Entity
@Table(name = "membership")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipEntity {
    /**
     * 멤버십의 데이터베이스 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long membershipId;

    /**
     * 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 이메일
     */
    @Column(nullable = false)
    private String email;

    /**
     * 주소
     */
    @Column(nullable = false)
    private String address;

    /**
     * 유효 상태 여부
     */
    @Column(nullable = false)
    private boolean isValid;

    /**
     * 법인 여부
     */
    @Column(nullable = false)
    private boolean isCorp;

    /**
     * 생성 시각
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
