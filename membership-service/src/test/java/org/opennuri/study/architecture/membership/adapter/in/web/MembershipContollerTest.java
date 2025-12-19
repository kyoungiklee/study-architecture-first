package org.opennuri.study.architecture.membership.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opennuri.study.architecture.membership.application.port.in.*;
import org.opennuri.study.architecture.membership.domain.Membership;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MembershipContollerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private RegisterMembershipUseCase registerMembershipUseCase;
    private UpdateMembershipUseCase updateMembershipUseCase;
    private GetMembershipByIdUseCase getMembershipByIdUseCase;
    private SearchMembershipUseCase searchMembershipUseCase;
    private DeleteMembershipUseCase deleteMembershipUseCase;

    @BeforeEach
    void setup() {
        registerMembershipUseCase = Mockito.mock(RegisterMembershipUseCase.class);
        updateMembershipUseCase = Mockito.mock(UpdateMembershipUseCase.class);
        getMembershipByIdUseCase = Mockito.mock(GetMembershipByIdUseCase.class);
        searchMembershipUseCase = Mockito.mock(SearchMembershipUseCase.class);
        deleteMembershipUseCase = Mockito.mock(DeleteMembershipUseCase.class);

        MembershipController controller = new MembershipController(
                registerMembershipUseCase,
                updateMembershipUseCase,
                getMembershipByIdUseCase,
                searchMembershipUseCase,
                deleteMembershipUseCase
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private Membership sample(String id) {
        return Membership.builder()
                .membershipId(id)
                .name("홍길동")
                .email("hong@example.com")
                .address("서울시 어딘가")
                .isCorp(false)
                .isValid(true)
                .build();
    }

    @Test
    @DisplayName("POST /memberships - 생성 성공 시 201과 Location, Body 반환")
    void create_success() throws Exception {
        Membership created = sample("1");
        Mockito.when(registerMembershipUseCase.registerMembership(any(RegisterMembershipCommand.class)))
                .thenReturn(created);

        RegisterMembershipRequest req = new RegisterMembershipRequest("홍길동", "hong@example.com", "서울시 어딘가", false);

        mockMvc.perform(post("/memberships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/memberships/1"))
                .andExpect(jsonPath("$.membershipId").value("1"))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.email").value("hong@example.com"))
                .andExpect(jsonPath("$.address").value("서울시 어딘가"))
                .andExpect(jsonPath("$.corp").value(false))
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    @DisplayName("GET /memberships/{id} - 존재하면 200과 Body 반환")
    void getById_found() throws Exception {
        Mockito.when(getMembershipByIdUseCase.getMembershipById("1"))
                .thenReturn(Optional.of(sample("1")));

        mockMvc.perform(get("/memberships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membershipId").value("1"));
    }

    @Test
    @DisplayName("GET /memberships/{id} - 없으면 404")
    void getById_notFound() throws Exception {
        Mockito.when(getMembershipByIdUseCase.getMembershipById("999"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/memberships/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /memberships - 검색은 200과 리스트 반환")
    void search_returnsList() throws Exception {
        List<Membership> list = Arrays.asList(sample("1"), sample("2"));
        Mockito.when(searchMembershipUseCase.searchMemberships(any(SearchMembershipQuery.class)))
                .thenReturn(list);

        mockMvc.perform(get("/memberships")
                        .param("name", "홍")
                        .param("isCorp", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].membershipId").value("1"))
                .andExpect(jsonPath("$[1].membershipId").value("2"));
    }

    @Test
    @DisplayName("PUT /memberships/{id} - 성공 시 200")
    void update_success() throws Exception {
        Membership updated = sample("1");
        Mockito.when(updateMembershipUseCase.updateMembership(any(UpdateMembershipCommand.class)))
                .thenReturn(updated);

        UpdateMembershipRequest req = new UpdateMembershipRequest("홍길동", "hong@example.com", "서울시 어딘가", false);

        mockMvc.perform(put("/memberships/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membershipId").value("1"));
    }

    @Test
    @DisplayName("PUT /memberships/{id} - 대상 없으면 404")
    void update_notFound() throws Exception {
        Mockito.when(updateMembershipUseCase.updateMembership(any(UpdateMembershipCommand.class)))
                .thenThrow(new IllegalArgumentException("not found"));

        UpdateMembershipRequest req = new UpdateMembershipRequest("홍길동", "hong@example.com", "서울시 어딘가", false);

        mockMvc.perform(put("/memberships/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /memberships/{id} - 성공 시 204")
    void delete_success() throws Exception {
        Mockito.when(deleteMembershipUseCase.deleteMembership(eq("1")))
                .thenReturn(true);

        mockMvc.perform(delete("/memberships/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /memberships/{id} - 없으면 404")
    void delete_notFound() throws Exception {
        Mockito.when(deleteMembershipUseCase.deleteMembership(eq("999")))
                .thenReturn(false);

        mockMvc.perform(delete("/memberships/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /memberships/test - 200 OK와 본문 OK")
    void testEndpoint() throws Exception {
        mockMvc.perform(get("/memberships/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
