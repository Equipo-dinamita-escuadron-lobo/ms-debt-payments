package debt_payments.infraestructure.input.rest.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptDetailRequest {
    @NotNull(message = "Invoice ID cannot be null")
    private Long invoiceId;
    
    @NotNull(message = "Amount paid cannot be null")
    @Positive(message = "Amount paid must be positive")
    private BigDecimal amountPaid;
}
