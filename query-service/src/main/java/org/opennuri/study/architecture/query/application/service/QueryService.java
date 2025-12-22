package org.opennuri.study.architecture.query.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.query.application.port.in.GetAggregatedSummaryUseCase;
import org.opennuri.study.architecture.query.application.port.out.GetMembershipPort;
import org.opennuri.study.archtecture.common.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryService implements GetAggregatedSummaryUseCase {

    private final GetMembershipPort getMembershipPort;

    @Override
    public Map<String, Object> getAggregatedSummary(String memberId) {
        // 실제 구현에서는 여러 포트를 병렬로 호출하여 데이터를 집계함
        Map<String, Object> summary = new HashMap<>();
        summary.put("memberId", memberId);
        summary.put("membership", getMembershipPort.getMembership(memberId));
        return summary;
    }
}
