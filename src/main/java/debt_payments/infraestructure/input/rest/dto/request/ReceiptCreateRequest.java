package debt_payments.infraestructure.input.rest.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptCreateRequest {

    @NotNull(message = "Third party ID cannot be null")
    private Long thirdPartyId;

    @NotNull(message = "Payment method ID cannot be null")
    private Long paymentMethodId;

    @NotNull(message = "Receipt type ID cannot be null")
    private Long receiptTypeId;

    private String observations;

    private Long ledgerAccountId;

    private String enterpriseId;

    @Valid
    @NotEmpty(message = "Receipt details cannot be empty for an invoice payment")
    private List<ReceiptDetailRequest> details;
}
