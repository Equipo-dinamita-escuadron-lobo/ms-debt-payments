package debt_payments.infraestructure.output.jpa.entity.replicas;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "third_replica")
public class ThirdPartyReplicaEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "identification_number", nullable = false)
    private String identificationNumber;

    @Column(name = "enterprise_id", nullable = false)
    private String enterpriseId;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "last_update_at", nullable = false)
    private LocalDate lastUpdateAt;
}
