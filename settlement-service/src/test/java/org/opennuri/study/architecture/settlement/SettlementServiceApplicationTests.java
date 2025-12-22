package org.opennuri.study.architecture.settlement;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SettlementServiceApplicationTests {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // 컨텍스트 로딩이 되면 테스트 통과
    }

    @Test
    void ping_returns_pong() {
        ResponseEntity<String> res = restTemplate.getForEntity("http://localhost:" + port + "/settlement/ping", String.class);
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).isEqualTo("pong");
    }
}
