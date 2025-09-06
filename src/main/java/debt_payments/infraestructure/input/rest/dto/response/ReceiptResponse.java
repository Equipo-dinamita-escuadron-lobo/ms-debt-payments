package debt_payments.infraestructure.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptResponse {
    private Long id;
    private String receiptCode;
    private Long thirdPartyId;
    private String status; 
    private LocalDateTime issueDate;
    private BigDecimal totalAmount;
    private String observations;
    private List<ReceiptDetailResponse> details;
}
