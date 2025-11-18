package org.opennuri.study.architecture.membership.adapter.out.persistance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true"
})
class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;

    private MembershipEntity saveMember(String name, String email, String address, boolean isCorp, boolean isValid) {
        MembershipEntity entity = MembershipEntity.builder()
                .name(name)
                .email(email)
                .address(address)
                .isCorp(isCorp)
                .isValid(isValid)
                .createdAt(LocalDateTime.now())
                .build();
        return membershipRepository.save(entity);
    }

    @Test
    @DisplayName("CRUD: save, findById, existsById, deleteById")
    void crud_basic() {
        MembershipEntity saved = saveMember("Alice", "alice@example.com", "Seoul", false, true);

        Optional<MembershipEntity> found = membershipRepository.findById(saved.getMembershipId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");

        assertThat(membershipRepository.existsById(saved.getMembershipId())).isTrue();

        membershipRepository.deleteById(saved.getMembershipId());
        assertThat(membershipRepository.findById(saved.getMembershipId())).isEmpty();
    }

    @Test
    @DisplayName("JPQL search: 부분일치/대소문자무시 + 불리언 필터")
    void search_with_jpql() {
        saveMember("Alice", "alice@example.com", "Seoul", false, true);
        saveMember("Bob", "bob@corp.com", "Busan", true, true);
        saveMember("Charlie", "charlie@sample.com", "Seoul", false, false);

        // name contains (case-insensitive)
        List<MembershipEntity> byName = membershipRepository.search("ali", null, null, null, null);
        assertThat(byName).extracting(MembershipEntity::getName).containsExactly("Alice");

        // address contains + isCorp true
        List<MembershipEntity> corpInBusan = membershipRepository.search(null, null, "busa", true, null);
        assertThat(corpInBusan).hasSize(1);
        assertThat(corpInBusan.get(0).getName()).isEqualTo("Bob");

        // isValid false only
        List<MembershipEntity> invalidOnly = membershipRepository.search(null, null, null, null, false);
        assertThat(invalidOnly).hasSize(1);
        assertThat(invalidOnly.get(0).getName()).isEqualTo("Charlie");
    }

    @Test
    @DisplayName("Example search: 문자열 CONTAINING + ignoreCase, 불리언 null 시 제외")
    void search_with_example() {
        saveMember("Alice", "alice@example.com", "Seoul", false, true);
        saveMember("Bob", "bob@corp.com", "Busan", true, true);
        saveMember("Bobby", "bobby@corp.com", "Busan", true, false);

        // name contains 'bo' (case-insensitive), 불리언 조건 없음
        List<MembershipEntity> list = membershipRepository.searchByExample("Bo", null, null, null, null);
        assertThat(list).extracting(MembershipEntity::getName)
                .containsExactlyInAnyOrder("Bob", "Bobby");

        // address contains 'bus' + isCorp=true 만 필터, isValid=null 은 제외
        List<MembershipEntity> busanCorp = membershipRepository.searchByExample(null, null, "bus", true, null);
        assertThat(busanCorp).extracting(MembershipEntity::getName)
                .containsExactlyInAnyOrder("Bob", "Bobby");
    }

    @Test
    @DisplayName("QueryDSL search: containsIgnoreCase + 불리언 정확 일치 + null 제외")
    void search_with_querydsl() {
        saveMember("Alice", "alice@example.com", "Seoul", false, true);
        saveMember("Bob", "bob@corp.com", "Busan", true, true);
        saveMember("Bobby", "bobby@corp.com", "Busan", true, false);

        // name contains 'bo' (대소문자 무시)
        List<MembershipEntity> nameBo = membershipRepository.searchUsingQuerydsl("bo", null, null, null, null);
        assertThat(nameBo).extracting(MembershipEntity::getName)
                .containsExactlyInAnyOrder("Bob", "Bobby");

        // Busan + isCorp=true + isValid=true
        List<MembershipEntity> busanCorpValid = membershipRepository.searchUsingQuerydsl(null, null, "bus", true, true);
        assertThat(busanCorpValid).extracting(MembershipEntity::getName)
                .containsExactly("Bob");
    }
}
