package org.opennuri.study.architecture.banking.application.port.in;

import org.opennuri.study.architecture.banking.application.port.in.command.RequestFirmbankingCommand;
import org.opennuri.study.architecture.banking.domain.Firmbanking;

/**
 * 펌뱅킹 요청 유스케이스
 */
public interface RequestFirmbankingUseCase {
    /**
     * 펌뱅킹 요청
     * @param command 펌뱅킹 요청 정보
     * @return 생성된 펌뱅킹
     */
    Firmbanking requestFirmbanking(RequestFirmbankingCommand command);
}
