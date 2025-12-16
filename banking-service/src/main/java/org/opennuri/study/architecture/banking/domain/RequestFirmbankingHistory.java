package org.opennuri.study.architecture.banking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFirmbankingHistory {
    private Long id;
    private Long requestFirmbankingId;
    private String status;
}
