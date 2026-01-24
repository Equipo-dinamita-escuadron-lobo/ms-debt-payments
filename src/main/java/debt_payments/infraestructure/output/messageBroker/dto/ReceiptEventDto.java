package debt_payments.infraestructure.output.messageBroker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptEventDto {
    private Long id;
    private String receiptCode;
    private Long thirdPartyId;
    private Long paymentMethodId;
    private Long paymentMethodAccount;
    private String enterpriseId;
    private Long receiptTypeId;
    private String status;
    private LocalDate issueDate;
    private BigDecimal totalAmount;
    private String observations;
    private Long ledgerAccountId;
    private Long centerCostId;
    private List<ReceiptDetailEventDto> details;
}
