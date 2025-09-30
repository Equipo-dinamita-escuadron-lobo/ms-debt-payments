package debt_payments.domain.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class Receipt {
    private Long id;
    private String receiptCode;
    private String enterpriseId;
    private Long thirdPartyId;
    private Long paymentMethodId;
    private Long paymentMethodAccount;
    private Long receiptTypeId;
    private ReceiptStatus status;
    private LocalDate issueDate;
    private Long totalAmount;
    private String observations;
    private String voidReasonDescription;
    private LocalDate voidDate;

    private Long ledgerAccountId; 
    private Long centerCostId;
    private List<ReceiptDetail> details;

    public boolean isInvoicePayment() {
        // Assuming ID 1 corresponds to "Invoice Payment"
        return this.receiptTypeId != null && this.receiptTypeId.equals(1L);
    }

}
