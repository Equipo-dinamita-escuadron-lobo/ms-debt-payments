package debt_payments.infraestructure.output.jpa.entity;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.TenantId;

import debt_payments.domain.enums.WriteOffStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "portfolio_write_offs")
@Getter
@Setter
public class PortfolioWriteOffEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String justification;

    @Column(nullable = false, length = 100)
    private String code;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "write_off_date", nullable = false)
    private LocalDate writeOffDate;

    @Column(name = "debit_auxiliary_account", nullable = false)
    private Long debitAuxiliaryAccount;

    @Column(name = "debit_auxiliary_account_id", nullable = false)
    private Long debitAuxiliaryAccountId;

    @Column(name = "third_id", nullable = false)
    private Long thirdId;

    @Enumerated(EnumType.STRING) // Guarda el estado como texto (e.g., "CONFIRMED")
    @Column(nullable = false)
    private WriteOffStatus status;

    @Column(name = "enterprise_id", nullable = false)
    private String enterpriseId; // Tu campo para multitenancy a nivel de negocio

    @OneToMany(
        mappedBy = "portfolioWriteOff", // "portfolioWriteOff" es el nombre del campo en WriteOffDetailEntity
        cascade = CascadeType.ALL,     // Cuando guardo/elimino un castigo, también afecta a sus detalles
        orphanRemoval = true,          // Si quito un detalle de la lista, se borra de la DB
        fetch = FetchType.EAGER        // Carga los detalles junto con la cabecera
    )
    private List<WriteOffDetailEntity> details;

    @TenantId
    String tenantId;
}