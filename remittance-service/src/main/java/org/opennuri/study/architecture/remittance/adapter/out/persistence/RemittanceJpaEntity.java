package org.opennuri.study.architecture.remittance.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.remittance.domain.model.RemittanceStatus;
import org.opennuri.study.architecture.remittance.domain.model.RemittanceType;

@Entity
@Table(name = "remittance")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemittanceJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String remittanceId;

    private String fromMembershipId;
    
    @Enumerated(EnumType.STRING)
    private RemittanceType toType;
    
    private String toTarget;
    
    private Long amount;
    
    private String reason;
    
    @Enumerated(EnumType.STRING)
    private RemittanceStatus status;
    
    private String failureCode;
    
    private Long createdAt;
    
    private Long updatedAt;
}
