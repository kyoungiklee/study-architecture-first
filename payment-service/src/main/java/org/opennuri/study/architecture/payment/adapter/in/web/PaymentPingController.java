package org.opennuri.study.architecture.payment.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentPingController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
