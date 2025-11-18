package org.opennuri.study.architecture.membership.adapter.out.persistance;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MembershipRepository extends JpaRepository<MembershipJpaEntity, Long>, MembershipRepositoryCustom {
    @Query("select m from MembershipJpaEntity m\n" +
            " where (:name is null or lower(m.name) like lower(concat('%', :name, '%')))\n" +
            "   and (:email is null or lower(m.email) like lower(concat('%', :email, '%')))\n" +
            "   and (:address is null or lower(m.address) like lower(concat('%', :address, '%')))\n" +
            "   and (:isCorp is null or m.isCorp = :isCorp)\n" +
            "   and (:isValid is null or m.isValid = :isValid)")
    List<MembershipJpaEntity> search(
            @Param("name") String name,
            @Param("email") String email,
            @Param("address") String address,
            @Param("isCorp") Boolean isCorp,
            @Param("isValid") Boolean isValid
    );

    /**
     * Example 기반 동적 검색 메서드.
     * - 문자열(name/email/address)은 대소문자 무시, 부분일치(CONTAINING)
     * - 불리언(isCorp/isValid)은 정확 일치, null 이면 필터에서 제외
     *
     * 주의: ExampleMatcher의 CONTAINING은 대소문자 무시를 전역으로 적용합니다.
     */
    default List<MembershipJpaEntity> searchByExample(String name,
                                                      String email,
                                                      String address,
                                                      Boolean isCorp,
                                                      Boolean isValid) {
        // Probe 생성 (문자열은 null 값 유지, 불리언은 필요시에만 세팅)
        MembershipJpaEntity.MembershipJpaEntityBuilder builder = MembershipJpaEntity.builder();
        if (name != null) builder.name(name);
        if (email != null) builder.email(email);
        if (address != null) builder.address(address);
        if (isCorp != null) builder.isCorp(isCorp);
        if (isValid != null) builder.isValid(isValid);
        MembershipJpaEntity probe = builder.build();

        // 매처 구성: null 은 무시, 문자열은 CONTAINING + ignoreCase
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withIgnorePaths("membershipId", "createdAt");

        // 문자열 공통 규칙
        matcher = matcher.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();

        // 불리언 필드가 null 이면 비교에서 제외해야 하므로 명시적으로 ignorePaths 설정
        if (isCorp == null) {
            matcher = matcher.withIgnorePaths("isCorp");
        }
        if (isValid == null) {
            matcher = matcher.withIgnorePaths("isValid");
        }

        Example<MembershipJpaEntity> example = Example.of(probe, matcher);
        return findAll(example);
    }
}
