package debt_payments.infraestructure.output.jpa.entity;

import org.hibernate.annotations.TenantId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "write_off_details")
@Getter
@Setter
public class WriteOffDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "amount_written_off", nullable = false)
    private Long amountWrittenOff;

    @Column(name = "accounting_account", nullable = false)
    private Long accountingAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "write_off_id", nullable = false)
    private PortfolioWriteOffEntity portfolioWriteOff;

    @TenantId
    String tenantId;
}
