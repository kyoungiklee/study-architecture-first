package org.opennuri.study.architecture.membership.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opennuri.study.architecture.membership.application.port.in.RegisterMembershipCommand;
import org.opennuri.study.architecture.membership.application.port.in.UpdateMembershipCommand;
import org.opennuri.study.architecture.membership.application.port.out.CommandMembershipPort;
import org.opennuri.study.architecture.membership.domain.Membership;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * MembershipCommandService 단위 테스트
 * - 포트(CommandMembershipPort)를 Mock 으로 대체하여 상호작용과 매핑을 검증한다.
 */
class MembershipCommandServiceTest {

    private CommandMembershipPort commandPort;
    private MembershipCommandService service;

    @BeforeEach
    void setUp() {
        commandPort = Mockito.mock(CommandMembershipPort.class);
        service = new MembershipCommandService(commandPort);
    }

    @Test
    @DisplayName("registerMembership: Command -> Domain 매핑 및 포트 호출 검증")
    void registerMembership_success() {
        // given
        RegisterMembershipCommand command = RegisterMembershipCommand.builder()
                .name("Alice")
                .email("alice@example.com")
                .address("Seoul")
                .isCorp(false)
                .build();

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);

        when(commandPort.crateMembership(any())).thenAnswer(invocation -> {
            Membership req = invocation.getArgument(0);
            // 저장 후 ID가 부여된 것으로 가정
            return Membership.builder()
                    .membershipId("generated-id-1")
                    .name(req.getName())
                    .email(req.getEmail())
                    .address(req.getAddress())
                    .isCorp(req.isCorp())
                    .isValid(req.isValid())
                    .build();
        });

        // when
        Membership result = service.registerMembership(command);

        // then
        verify(commandPort, times(1)).crateMembership(captor.capture());
        Membership passed = captor.getValue();
        assertThat(passed.getMembershipId()).isNull();
        assertThat(passed.getName()).isEqualTo("Alice");
        assertThat(passed.getEmail()).isEqualTo("alice@example.com");
        assertThat(passed.getAddress()).isEqualTo("Seoul");
        assertThat(passed.isCorp()).isFalse();
        // RegisterMembershipCommand 내부 validateSelf() 이후 isValid=true 로 설정됨 → 그대로 전달되었는지 확인
        assertThat(passed.isValid()).isTrue();

        assertThat(result.getMembershipId()).isEqualTo("generated-id-1");
        assertThat(result.getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("updateMembership: Command -> Domain 매핑 및 포트 호출 검증")
    void updateMembership_success() {
        // given
        UpdateMembershipCommand command = UpdateMembershipCommand.builder()
                .membershipId("mem-123")
                .name("Bob")
                .email("bob@example.com")
                .address("Busan")
                .isCorp(true)
                .build();

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);

        when(commandPort.updateMembership(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Membership updated = service.updateMembership(command);

        // then
        verify(commandPort, times(1)).updateMembership(captor.capture());
        Membership passed = captor.getValue();
        assertThat(passed.getMembershipId()).isEqualTo("mem-123");
        assertThat(passed.getName()).isEqualTo("Bob");
        assertThat(passed.getEmail()).isEqualTo("bob@example.com");
        assertThat(passed.getAddress()).isEqualTo("Busan");
        assertThat(passed.isCorp()).isTrue();
        // 서비스 로직에서는 항상 isValid=true 로 설정
        assertThat(passed.isValid()).isTrue();

        assertThat(updated).isEqualTo(passed);
    }

    @Test
    @DisplayName("deleteMembership: 포트 호출 및 반환값 위임 검증")
    void deleteMembership_success() {
        // given
        when(commandPort.deleteById("mem-999")).thenReturn(true);

        // when
        boolean result = service.deleteMembership("mem-999");

        // then
        verify(commandPort, times(1)).deleteById("mem-999");
        assertThat(result).isTrue();
    }
}
