package org.opennuri.study.architecture.membership.adapter.out.persistance;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.membership.application.port.out.CommandMembershipPort;
import org.opennuri.study.architecture.membership.application.port.out.QueryMembershipPort;
import org.opennuri.study.architecture.membership.domain.Membership;
import org.opennuri.study.archtecture.common.PersistanceAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 멤버십 영속성 처리를 담당하는 어댑터입니다.
 * 아웃바운드 포트(CommandMembershipPort, QueryMembershipPort)를 구현하며,
 * JPA 리포지토리를 사용하여 실제 데이터베이스와 상호작용합니다.
 */
@PersistanceAdapter
@RequiredArgsConstructor
public class MembershipPersistenceAdapter implements CommandMembershipPort, QueryMembershipPort {

    private final MembershipRepository membershipRepository;

    /**
     * 새로운 멤버십 엔티티를 생성하고 데이터베이스에 저장합니다.
     *
     * @param membership 저장할 멤버십 도메인 모델
     * @return 저장된 멤버십 도메인 모델 (ID 포함)
     */
    @Override
    public Membership crateMembership(Membership membership) {
        // MembershipEntity에는 Lombok @Builder가 없으므로, 생성 후 setter로 값 설정
        MembershipEntity entity = new MembershipEntity();
        entity.setName(membership.getName());
        entity.setEmail(membership.getEmail());
        entity.setAddress(membership.getAddress());
        entity.setCorp(membership.isCorp());
        entity.setValid(membership.isValid());
        entity.setCreatedAt(LocalDateTime.now());

        MembershipEntity saved = membershipRepository.save(entity);

        return Membership.builder()
                .membershipId(String.valueOf(saved.getMembershipId()))
                .name(saved.getName())
                .email(saved.getEmail())
                .address(saved.getAddress())
                .isCorp(saved.isCorp())
                .isValid(saved.isValid())
                .build();
    }

    /**
     * 기존 멤버십 정보를 업데이트합니다.
     *
     * @param membership 수정할 정보가 담긴 도메인 모델
     * @return 업데이트된 멤버십 도메인 모델
     */
    @Override
    public Membership updateMembership(Membership membership) {
        // membershipId 는 문자열이므로 Long 으로 변환
        Long id = Long.parseLong(membership.getMembershipId());
        Optional<MembershipEntity> optional = membershipRepository.findById(id);
        MembershipEntity entity = optional.orElseThrow(() -> new IllegalArgumentException("Membership not found: " + id));

        // 필드 업데이트 (createdAt은 생성 시각으로 유지)
        entity.setName(membership.getName());
        entity.setEmail(membership.getEmail());
        entity.setAddress(membership.getAddress());
        entity.setCorp(membership.isCorp());
        entity.setValid(membership.isValid());

        MembershipEntity updated = membershipRepository.save(entity);

        return Membership.builder()
                .membershipId(String.valueOf(updated.getMembershipId()))
                .name(updated.getName())
                .email(updated.getEmail())
                .address(updated.getAddress())
                .isCorp(updated.isCorp())
                .isValid(updated.isValid())
                .build();
    }

    /**
     * ID로 멤버십 엔티티를 조회합니다.
     *
     * @param membershipId 멤버십 ID (문자열)
     * @return 조회된 멤버십 도메인 모델 (Optional)
     */
    @Override
    public Optional<Membership> findById(String membershipId) {
        Long id = Long.parseLong(membershipId);
        return membershipRepository.findById(id)
                .map(saved -> Membership.builder()
                        .membershipId(String.valueOf(saved.getMembershipId()))
                        .name(saved.getName())
                        .email(saved.getEmail())
                        .address(saved.getAddress())
                        .isCorp(saved.isCorp())
                        .isValid(saved.isValid())
                        .build());
    }

    /**
     * 다양한 조건으로 멤버십을 검색합니다.
     * QueryDSL을 사용하여 동적 쿼리를 처리합니다.
     *
     * @param name 이름
     * @param email 이메일
     * @param address 주소
     * @param isCorp 법인 여부
     * @param isValid 유효 상태
     * @return 검색된 멤버십 도메인 모델 리스트
     */
    @Override
    public List<Membership> search(String name, String email, String address, Boolean isCorp, Boolean isValid) {
        // 프로젝트 표준: QueryDSL 기반 검색 사용
        return membershipRepository.searchUsingQuerydsl(name, email, address, isCorp, isValid)
                .stream()
                .map(saved -> Membership.builder()
                        .membershipId(String.valueOf(saved.getMembershipId()))
                        .name(saved.getName())
                        .email(saved.getEmail())
                        .address(saved.getAddress())
                        .isCorp(saved.isCorp())
                        .isValid(saved.isValid())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * ID에 해당하는 멤버십을 삭제합니다.
     *
     * @param membershipId 삭제할 멤버십 ID
     * @return 삭제 성공 시 true, 존재하지 않으면 false
     */
    @Override
    public boolean deleteById(String membershipId) {
        Long id = Long.parseLong(membershipId);
        if (!membershipRepository.existsById(id)) {
            return false;
        }
        membershipRepository.deleteById(id);
        return true;
    }
}
