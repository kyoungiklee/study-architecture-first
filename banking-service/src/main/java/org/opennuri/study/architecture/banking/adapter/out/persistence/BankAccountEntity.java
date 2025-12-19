package org.opennuri.study.architecture.banking.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 은행 계좌 정보를 데이터베이스에 저장하기 위한 JPA 엔티티 클래스입니다.
 */
@Entity
@Table(name = "bank_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountEntity {

    /**
     * 계좌 엔티티의 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 계좌 소유자 회원 ID
     */
    @Column(nullable = false)
    private Long memberId;

    /**
     * 은행 코드
     */
    @Column(nullable = false, length = 20)
    private String bankCode;

    /**
     * 계좌 번호
     */
    @Column(nullable = false, length = 50)
    private String bankAccountNo;

    /**
     * 유효 상태 여부
     */
    @Column(nullable = false)
    private boolean valid;

    /**
     * 생성 시각
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 시각
     */
    @Column
    private LocalDateTime updatedAt;
}
