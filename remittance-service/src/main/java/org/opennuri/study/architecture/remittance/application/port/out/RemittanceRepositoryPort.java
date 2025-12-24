package org.opennuri.study.architecture.remittance.application.port.out;

import org.opennuri.study.architecture.remittance.domain.model.Remittance;

public interface RemittanceRepositoryPort {
    Remittance save(Remittance remittance);
    Remittance findByRemittanceId(String remittanceId);
}
