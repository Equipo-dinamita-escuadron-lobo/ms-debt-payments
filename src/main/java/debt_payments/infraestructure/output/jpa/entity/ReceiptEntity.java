package debt_payments.infraestructure.output.jpa.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import debt_payments.domain.model.ReceiptStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "receipts")
@Getter
@Setter
public class ReceiptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receipt_code", nullable = false, unique = true)
    private String receiptCode;

    @Column(name = "third_party_id", nullable = false)
    private Long thirdPartyId;

    @Column(name = "enterprise_id", nullable = false)
    private String enterpriseId;

    @Column(name = "payment_method_id", nullable = false)
    private Long paymentMethodId;

    @Column(name = "payment_method_account", nullable = false)
    private Long paymentMethodAccount;

    @Column(name = "receipt_type", nullable = false)
    private Long receiptTypeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReceiptStatus status;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "observations")
    private String observations;

    @Column(name = "void_reason_description", length = 500)
    private String voidReasonDescription;

    @Column(name = "void_date")      
    private LocalDateTime voidDate;

    @Column(name = "ledger_account")
    private Long ledgerAccountId;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceiptDetailEntity> details = new ArrayList<>();
}
