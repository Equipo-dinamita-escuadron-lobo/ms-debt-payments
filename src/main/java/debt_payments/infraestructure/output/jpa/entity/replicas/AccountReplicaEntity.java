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
@Table(name = "account_replica")
public class AccountReplicaEntity {
    @Id
    private Long accountId;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "account_type", nullable = false)
    private String accountType;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "enterprise_id", nullable = false)
    private Long enterpriseId;

    @Column(name = "last_update_at", nullable = false)
    private LocalDate lastUpdateAt;
}
