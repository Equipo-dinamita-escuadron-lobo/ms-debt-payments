package debt_payments.infraestructure.input.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO que representa un detalle de factura dentro de la petición de creación de un castigo.
 */
@Getter
@Setter
public class WriteOffDetailRequest {
    @NotNull(message = "Invoice ID cannot be null.")
    @Positive(message = "Invoice ID must be a positive number.")
    private Long invoiceId;
}
