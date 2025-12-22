package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.Firmbanking;

import java.util.List;
import java.util.Optional;

/**
 * 펌뱅킹 조회 포트
 */
public interface QueryFirmbankingPort {
    Optional<Firmbanking> findById(Long id);
    List<Firmbanking> findByMemberId(Long memberId);
}
