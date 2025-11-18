package org.opennuri.study.architecture.membership.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opennuri.study.architecture.membership.application.port.in.SearchMembershipQuery;
import org.opennuri.study.architecture.membership.application.port.out.QueryMembershipPort;
import org.opennuri.study.architecture.membership.domain.Membership;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * MembershipQueryService 단위 테스트
 */
class MembershipQueryServiceTest {

    private QueryMembershipPort queryPort;
    private MembershipQueryService service;

    @BeforeEach
    void setUp() {
        queryPort = Mockito.mock(QueryMembershipPort.class);
        service = new MembershipQueryService(queryPort);
    }

    @Test
    @DisplayName("getMembershipById: 존재 시 Optional.of 반환")
    void getMembershipById_present() {
        Membership m = Membership.builder()
                .membershipId("mem-1")
                .name("Test")
                .email("t@example.com")
                .address("Seoul")
                .isCorp(false)
                .isValid(true)
                .build();

        when(queryPort.findById("mem-1")).thenReturn(Optional.of(m));

        Optional<Membership> result = service.getMembershipById("mem-1");

        assertThat(result).isPresent();
        assertThat(result.get().getMembershipId()).isEqualTo("mem-1");
        verify(queryPort, times(1)).findById("mem-1");
    }

    @Test
    @DisplayName("getMembershipById: 미존재 시 Optional.empty 반환")
    void getMembershipById_empty() {
        when(queryPort.findById("none")).thenReturn(Optional.empty());

        Optional<Membership> result = service.getMembershipById("none");

        assertThat(result).isEmpty();
        verify(queryPort, times(1)).findById("none");
    }

    @Test
    @DisplayName("searchMemberships: 쿼리 파라미터 전달 및 목록 반환 검증")
    void searchMemberships_success() {
        SearchMembershipQuery query = SearchMembershipQuery.builder()
                .name("hong")
                .email(null)
                .address("seoul")
                .isCorp(false)
                .isValid(null)
                .build();

        Membership m1 = Membership.builder().membershipId("1").name("hong1").email("a").address("seoul").isCorp(false).isValid(true).build();
        Membership m2 = Membership.builder().membershipId("2").name("hong2").email("b").address("seoul").isCorp(false).isValid(true).build();

        ArgumentCaptor<String> nameCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> emailCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> addrCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> corpCap = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Boolean> validCap = ArgumentCaptor.forClass(Boolean.class);

        when(queryPort.search(any(), any(), any(), any(), any())).thenReturn(Arrays.asList(m1, m2));

        List<Membership> result = service.searchMemberships(query);

        assertThat(result).hasSize(2);

        verify(queryPort, times(1)).search(nameCap.capture(), emailCap.capture(), addrCap.capture(), corpCap.capture(), validCap.capture());
        assertThat(nameCap.getValue()).isEqualTo("hong");
        assertThat(emailCap.getValue()).isNull();
        assertThat(addrCap.getValue()).isEqualTo("seoul");
        assertThat(corpCap.getValue()).isFalse();
        assertThat(validCap.getValue()).isNull();
    }
}
