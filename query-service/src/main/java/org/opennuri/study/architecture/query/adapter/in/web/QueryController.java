package org.opennuri.study.architecture.query.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.query.application.port.in.GetAggregatedSummaryUseCase;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@WebAdapter
@RestController
@RequiredArgsConstructor
public class QueryController {

    private final GetAggregatedSummaryUseCase getAggregatedSummaryUseCase;

    @GetMapping("/query/member-summary/{memberId}")
    public Map<String, Object> getMemberSummary(@PathVariable String memberId) {
        return getAggregatedSummaryUseCase.getAggregatedSummary(memberId);
    }
}
