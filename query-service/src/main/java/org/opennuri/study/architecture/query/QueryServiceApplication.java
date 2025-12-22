package org.opennuri.study.architecture.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class QueryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QueryServiceApplication.class, args);
    }
}
