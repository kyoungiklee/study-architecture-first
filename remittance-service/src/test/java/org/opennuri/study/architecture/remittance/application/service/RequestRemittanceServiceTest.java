package org.opennuri.study.architecture.remittance.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opennuri.study.architecture.remittance.adapter.in.web.dto.RemitResponse;
import org.opennuri.study.architecture.remittance.application.port.in.RequestRemittanceUseCase;
import org.opennuri.study.architecture.remittance.application.port.out.*;
import org.opennuri.study.architecture.remittance.domain.model.RemittanceType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class RequestRemittanceServiceTest {

    private RequestRemittanceService requestRemittanceService;
    private LoadMembershipPort loadMembershipPort;
    private ReserveMoneyPort reserveMoneyPort;
    private CommitMoneyPort commitMoneyPort;
    private RollbackMoneyPort rollbackMoneyPort;
    private RequestFirmbankingPort requestFirmbankingPort;

    @BeforeEach
    void setUp() {
        loadMembershipPort = Mockito.mock(LoadMembershipPort.class);
        reserveMoneyPort = Mockito.mock(ReserveMoneyPort.class);
        commitMoneyPort = Mockito.mock(CommitMoneyPort.class);
        rollbackMoneyPort = Mockito.mock(RollbackMoneyPort.class);
        requestFirmbankingPort = Mockito.mock(RequestFirmbankingPort.class);

        requestRemittanceService = new RequestRemittanceService(
                loadMembershipPort,
                reserveMoneyPort,
                commitMoneyPort,
                rollbackMoneyPort,
                requestFirmbankingPort
        );
    }

    @Test
    void testRequestRemittance_MemberToMember_Success() {
        // given
        given(loadMembershipPort.loadMembershipStatus(anyString()))
                .willReturn(new LoadMembershipPort.MembershipStatus("m-100", true));
        given(reserveMoneyPort.reserveMoney(anyString(), any(), anyString()))
                .willReturn(new ReserveMoneyPort.MoneyReservation("res-1", true));
        given(commitMoneyPort.commitMoney(anyString())).willReturn(true);

        RequestRemittanceUseCase.RemittanceCommand command = RequestRemittanceUseCase.RemittanceCommand.builder()
                .fromMembershipId("m-100")
                .toType(RemittanceType.MEMBER)
                .toMembershipId("m-200")
                .amount(1000L)
                .idempotencyKey("idem-1")
                .build();

        // when
        RemitResponse response = requestRemittanceService.requestRemittance(command);

        // then
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        Mockito.verify(commitMoneyPort).commitMoney("res-1");
    }

    @Test
    void testRequestRemittance_MemberToBank_Success() {
        // given
        given(loadMembershipPort.loadMembershipStatus(anyString()))
                .willReturn(new LoadMembershipPort.MembershipStatus("m-100", true));
        given(reserveMoneyPort.reserveMoney(anyString(), any(), anyString()))
                .willReturn(new ReserveMoneyPort.MoneyReservation("res-2", true));
        given(requestFirmbankingPort.requestFirmbanking(any()))
                .willReturn(new RequestFirmbankingPort.FirmbankingResult(true));
        given(commitMoneyPort.commitMoney(anyString())).willReturn(true);

        RequestRemittanceUseCase.RemittanceCommand command = RequestRemittanceUseCase.RemittanceCommand.builder()
                .fromMembershipId("m-100")
                .toType(RemittanceType.BANK)
                .toBankCode("004")
                .toBankAccountNumber("123-456")
                .amount(1000L)
                .idempotencyKey("idem-2")
                .build();

        // when
        RemitResponse response = requestRemittanceService.requestRemittance(command);

        // then
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        Mockito.verify(requestFirmbankingPort).requestFirmbanking(any());
        Mockito.verify(commitMoneyPort).commitMoney("res-2");
    }
}
