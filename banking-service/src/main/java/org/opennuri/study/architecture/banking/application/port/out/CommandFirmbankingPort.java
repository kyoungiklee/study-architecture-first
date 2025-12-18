package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.Firmbanking;

/**
 * 펌뱅킹 명령 포트
 */
public interface CommandFirmbankingPort {
    Firmbanking save(Firmbanking firmbanking);
}
