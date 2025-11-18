package org.opennuri.study.architecture.membership.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.opennuri.study.architecture.membership.application.port.in.*;
import org.opennuri.study.architecture.membership.domain.Membership;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebAdapter
@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final RegisterMembershipUseCase registerMembershipUseCase;
    private final UpdateMembershipUseCase updateMembershipUseCase;
    private final GetMembershipByIdUseCase getMembershipByIdUseCase;
    private final SearchMembershipUseCase searchMembershipUseCase;
    private final DeleteMembershipUseCase deleteMembershipUseCase;

    @GetMapping("/membership/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("OK");
    }

    // Create
    @PostMapping("/membership")
    public ResponseEntity<MembershipResponse> create(@Valid @RequestBody RegisterMembershipRequest request) {
        RegisterMembershipCommand command = RegisterMembershipCommand.builder()
                .name(request.getName())
                .email(request.getEmail())
                .address(request.getAddress())
                .isCorp(request.isCorp())
                .build();

        Membership created = registerMembershipUseCase.registerMembership(command);
        MembershipResponse body = MembershipResponse.from(created);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/membership/" + created.getMembershipId()));
        return new ResponseEntity<>(body, headers, HttpStatus.CREATED);
    }

    // Read by id
    @GetMapping("/membership/{id}")
    public ResponseEntity<MembershipResponse> getById(@PathVariable("id") String id) {
        Optional<Membership> optional = getMembershipByIdUseCase.getMembershipById(id);
        return optional
                .map(MembershipResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Search (query)
    @GetMapping("/membership")
    public ResponseEntity<List<MembershipResponse>> search(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "isCorp", required = false) Boolean isCorp,
            @RequestParam(value = "isValid", required = false) Boolean isValid
    ) {
        SearchMembershipQuery query = SearchMembershipQuery.builder()
                .name(name)
                .email(email)
                .address(address)
                .isCorp(isCorp)
                .isValid(isValid)
                .build();
        List<MembershipResponse> result = searchMembershipUseCase.searchMemberships(query)
                .stream()
                .map(MembershipResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // Update
    @PutMapping("/membership/{id}")
    public ResponseEntity<MembershipResponse> update(@PathVariable("id") String id,
                                                     @Valid @RequestBody UpdateMembershipRequest request) {
        try {
            UpdateMembershipCommand command = UpdateMembershipCommand.builder()
                    .membershipId(id)
                    .name(request.getName())
                    .email(request.getEmail())
                    .address(request.getAddress())
                    .isCorp(request.isCorp())
                    .build();
            Membership updated = updateMembershipUseCase.updateMembership(command);
            return ResponseEntity.ok(MembershipResponse.from(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete
    @DeleteMapping("/membership/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        boolean deleted = deleteMembershipUseCase.deleteMembership(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }
}