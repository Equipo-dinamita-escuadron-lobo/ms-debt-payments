package debt_payments.infraestructure.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoidReceiptRequest {
    @NotBlank(message = "The reason for voiding cannot be blank.")
    private String reason;
}
