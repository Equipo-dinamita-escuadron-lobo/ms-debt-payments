package debt_payments.infraestructure.input.rest.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptDetailResponse {
    private Long invoiceId;           
    private BigDecimal amountPaid;   
}
