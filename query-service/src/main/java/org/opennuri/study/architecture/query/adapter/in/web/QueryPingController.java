package org.opennuri.study.architecture.query.adapter.in.web;

import org.opennuri.study.archtecture.common.WebAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebAdapter
@RestController
public class QueryPingController {

    @GetMapping("/query/ping")
    public String ping() {
        return "pong";
    }
}
