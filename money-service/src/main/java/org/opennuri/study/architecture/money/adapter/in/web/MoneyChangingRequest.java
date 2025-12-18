package org.opennuri.study.architecture.money.adapter.in.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoneyChangingRequest {
    private String targetMembershipId;
    private Long amount;
}
