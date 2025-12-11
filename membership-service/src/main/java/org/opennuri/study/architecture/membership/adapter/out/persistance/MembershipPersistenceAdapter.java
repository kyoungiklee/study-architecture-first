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

@PersistanceAdapter
@RequiredArgsConstructor
public class MembershipPersistenceAdapter implements CommandMembershipPort, QueryMembershipPort {

    private final MembershipRepository membershipRepository;

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
