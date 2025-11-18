package org.opennuri.study.architecture.membership.adapter.out.persistance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opennuri.study.architecture.membership.domain.Membership;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true"
})
@Import(MembershipPersistenceAdapter.class)
class MembershipPersistenceAdapterTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipPersistenceAdapter adapter;

    private Membership newDomain(String name, String email, String address, boolean isCorp, boolean isValid) {
        return Membership.builder()
                .name(name)
                .email(email)
                .address(address)
                .isCorp(isCorp)
                .isValid(isValid)
                .build();
    }

    @Test
    @DisplayName("crateMembership: 도메인 -> 엔티티 저장 및 도메인 매핑 반환")
    void create_membership() {
        // given
        Membership toCreate = newDomain("Alice", "alice@example.com", "Seoul", false, true);

        // when
        Membership saved = adapter.crateMembership(toCreate);

        // then
        assertThat(saved.getMembershipId()).isNotBlank();
        assertThat(saved.getName()).isEqualTo("Alice");
        assertThat(saved.getEmail()).isEqualTo("alice@example.com");
        assertThat(saved.getAddress()).isEqualTo("Seoul");
        assertThat(saved.isCorp()).isFalse();
        assertThat(saved.isValid()).isTrue();

        // DB에도 저장되었는지 확인
        Long id = Long.parseLong(saved.getMembershipId());
        assertThat(membershipRepository.findById(id)).isPresent();
    }

    @Test
    @DisplayName("updateMembership: 존재하는 멤버 업데이트 및 매핑 검증")
    void update_membership() {
        // given - 선 저장
        Membership created = adapter.crateMembership(newDomain("Bob", "bob@corp.com", "Busan", true, true));
        // update 도메인
        Membership toUpdate = Membership.builder()
                .membershipId(created.getMembershipId())
                .name("Bobby")
                .email("bobby@corp.com")
                .address("Seoul")
                .isCorp(true)
                .isValid(false)
                .build();

        // when
        Membership updated = adapter.updateMembership(toUpdate);

        // then
        assertThat(updated.getMembershipId()).isEqualTo(created.getMembershipId());
        assertThat(updated.getName()).isEqualTo("Bobby");
        assertThat(updated.getEmail()).isEqualTo("bobby@corp.com");
        assertThat(updated.getAddress()).isEqualTo("Seoul");
        assertThat(updated.isCorp()).isTrue();
        assertThat(updated.isValid()).isFalse();

        // DB 반영 확인
        Long id = Long.parseLong(updated.getMembershipId());
        MembershipEntity entity = membershipRepository.findById(id).orElseThrow();
        assertThat(entity.getName()).isEqualTo("Bobby");
        assertThat(entity.getEmail()).isEqualTo("bobby@corp.com");
        assertThat(entity.getAddress()).isEqualTo("Seoul");
        assertThat(entity.isCorp()).isTrue();
        assertThat(entity.isValid()).isFalse();
    }

    @Test
    @DisplayName("findById: 존재/미존재 케이스 Optional 매핑")
    void find_by_id() {
        // given
        Membership created = adapter.crateMembership(newDomain("Charlie", "charlie@sample.com", "Seoul", false, true));
        String id = created.getMembershipId();

        // when
        Optional<Membership> found = adapter.findById(id);
        Optional<Membership> notFound = adapter.findById("999999");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("charlie@sample.com");
        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("search: QueryDSL 기반 필터링 결과가 도메인으로 매핑되어 반환")
    void search_querydsl_through_adapter() {
        adapter.crateMembership(newDomain("Alice", "alice@example.com", "Seoul", false, true));
        adapter.crateMembership(newDomain("Bob", "bob@corp.com", "Busan", true, true));
        adapter.crateMembership(newDomain("Bobby", "bobby@corp.com", "Busan", true, false));

        // name contains 'bo'
        List<Membership> byName = adapter.search("bo", null, null, null, null);
        assertThat(byName).extracting(Membership::getName)
                .containsExactlyInAnyOrder("Bob", "Bobby");

        // Busan + isCorp=true + isValid=true
        List<Membership> busanCorpValid = adapter.search(null, null, "bus", true, true);
        assertThat(busanCorpValid).extracting(Membership::getName)
                .containsExactly("Bob");
    }

    @Test
    @DisplayName("deleteById: 존재하면 true, 삭제되고, 미존재는 false")
    void delete_by_id() {
        // given
        Membership created = adapter.crateMembership(newDomain("Dave", "dave@corp.com", "Incheon", true, true));
        String id = created.getMembershipId();

        // when / then
        boolean deleted = adapter.deleteById(id);
        assertThat(deleted).isTrue();
        assertThat(membershipRepository.findById(Long.parseLong(id))).isEmpty();

        // 존재하지 않는 경우
        assertThat(adapter.deleteById("999999")).isFalse();
    }
}
