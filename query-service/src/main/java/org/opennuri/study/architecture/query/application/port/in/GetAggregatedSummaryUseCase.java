package org.opennuri.study.architecture.query.application.port.in;

import java.util.Map;

public interface GetAggregatedSummaryUseCase {
    Map<String, Object> getAggregatedSummary(String memberId);
}
