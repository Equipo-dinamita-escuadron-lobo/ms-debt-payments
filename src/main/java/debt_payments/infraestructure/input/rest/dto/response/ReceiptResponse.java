package debt_payments.infraestructure.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptResponse {
    private Long id;
    private String receiptCode;
    private Long thirdPartyId;
    private Long paymentMethodId;
    private String status; 
    private LocalDate issueDate;
    private BigDecimal totalAmount;
    private String observations;
    private List<ReceiptDetailResponse> details;
}
