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
@Table(name = "method_payment_replicas")
public class MethodPaymentReplicaEntity {
    @Id
    private Long methodPaymentId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "enterprise_id", nullable = false)
    private Long enterpriseId;

    @Column(name = "last_update_at", nullable = false)
    private LocalDate lastUpdateAt;
}
