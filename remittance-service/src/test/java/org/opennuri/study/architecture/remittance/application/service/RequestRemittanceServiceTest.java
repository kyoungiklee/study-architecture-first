package org.opennuri.study.architecture.remittance.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opennuri.study.architecture.remittance.adapter.in.web.dto.RemitResponse;
import org.opennuri.study.architecture.remittance.application.port.in.RequestRemittanceUseCase;
import org.opennuri.study.architecture.remittance.application.port.out.*;
import org.opennuri.study.architecture.remittance.domain.model.Remittance;
import org.opennuri.study.architecture.remittance.domain.model.RemittanceStatus;
import org.opennuri.study.architecture.remittance.domain.model.RemittanceType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

class RequestRemittanceServiceTest {

    private RequestRemittanceService requestRemittanceService;
    private LoadMembershipPort loadMembershipPort;
    private ReserveMoneyPort reserveMoneyPort;
    private CommitMoneyPort commitMoneyPort;
    private RollbackMoneyPort rollbackMoneyPort;
    private RequestFirmbankingPort requestFirmbankingPort;
    private RemittanceRepositoryPort remittanceRepositoryPort;

    @BeforeEach
    void setUp() {
        loadMembershipPort = Mockito.mock(LoadMembershipPort.class);
        reserveMoneyPort = Mockito.mock(ReserveMoneyPort.class);
        commitMoneyPort = Mockito.mock(CommitMoneyPort.class);
        rollbackMoneyPort = Mockito.mock(RollbackMoneyPort.class);
        requestFirmbankingPort = Mockito.mock(RequestFirmbankingPort.class);
        remittanceRepositoryPort = Mockito.mock(RemittanceRepositoryPort.class);

        requestRemittanceService = new RequestRemittanceService(
                loadMembershipPort,
                reserveMoneyPort,
                commitMoneyPort,
                rollbackMoneyPort,
                requestFirmbankingPort,
                remittanceRepositoryPort
        );
    }

    @Test
    void testRequestRemittance_MemberToMember_Success() {
        // given
        given(remittanceRepositoryPort.save(any(Remittance.class)))
                .willAnswer(invocation -> {
                    Remittance r = invocation.getArgument(0);
                    if (r.getRemittanceId() == null) {
                        return Remittance.createRemittance(
                                "rem-1",
                                r.getFromMembershipId(),
                                r.getToType(),
                                r.getToTarget(),
                                r.getAmount(),
                                r.getReason(),
                                r.getStatus(),
                                r.getCreatedAt()
                        );
                    }
                    return r;
                });

        given(loadMembershipPort.loadMembershipStatus(anyString()))
                .willReturn(new LoadMembershipPort.MembershipStatus("m-100", true));
        given(reserveMoneyPort.reserveMoney(anyString(), anyLong(), anyString()))
                .willReturn(new ReserveMoneyPort.MoneyReservation("res-1", true));
        given(commitMoneyPort.commitMoney(anyString())).willReturn(true);

        RequestRemittanceUseCase.RemittanceCommand command = RequestRemittanceUseCase.RemittanceCommand.builder()
                .fromMembershipId("m-100")
                .toType(RemittanceType.MEMBERSHIP)
                .toTarget("m-200")
                .amount(1000L)
                .reason("gift")
                .build();

        // when
        RemitResponse response = requestRemittanceService.requestRemittance(command);

        // then
        assertThat(response.getStatus()).isEqualTo(RemittanceStatus.SUCCESS.name());
        Mockito.verify(commitMoneyPort).commitMoney("res-1");
    }

    @Test
    void testRequestRemittance_MemberToBank_Success() {
        // given
        given(remittanceRepositoryPort.save(any(Remittance.class)))
                .willAnswer(invocation -> {
                    Remittance r = invocation.getArgument(0);
                    if (r.getRemittanceId() == null) {
                        return Remittance.createRemittance(
                                "rem-2",
                                r.getFromMembershipId(),
                                r.getToType(),
                                r.getToTarget(),
                                r.getAmount(),
                                r.getReason(),
                                r.getStatus(),
                                r.getCreatedAt()
                        );
                    }
                    return r;
                });

        given(loadMembershipPort.loadMembershipStatus(anyString()))
                .willReturn(new LoadMembershipPort.MembershipStatus("m-100", true));
        given(reserveMoneyPort.reserveMoney(anyString(), anyLong(), anyString()))
                .willReturn(new ReserveMoneyPort.MoneyReservation("res-2", true));
        given(requestFirmbankingPort.requestFirmbanking(any()))
                .willReturn(new RequestFirmbankingPort.FirmbankingResult(true));
        given(commitMoneyPort.commitMoney(anyString())).willReturn(true);

        RequestRemittanceUseCase.RemittanceCommand command = RequestRemittanceUseCase.RemittanceCommand.builder()
                .fromMembershipId("m-100")
                .toType(RemittanceType.BANK_ACCOUNT)
                .toTarget("004:123-456")
                .amount(1000L)
                .reason("pay")
                .build();

        // when
        RemitResponse response = requestRemittanceService.requestRemittance(command);

        // then
        assertThat(response.getStatus()).isEqualTo(RemittanceStatus.SUCCESS.name());
        Mockito.verify(requestFirmbankingPort).requestFirmbanking(any());
        Mockito.verify(commitMoneyPort).commitMoney("res-2");
    }
}
