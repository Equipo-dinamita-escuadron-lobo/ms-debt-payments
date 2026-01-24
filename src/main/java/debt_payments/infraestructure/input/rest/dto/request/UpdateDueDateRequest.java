package debt_payments.infraestructure.input.rest.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * DTO for request body when updating the due date of a invoice
 */

@Getter
public class UpdateDueDateRequest {
    @NotNull(message = "La nueva fecha de vencimiento no puede ser nula.")
    private LocalDate newDueDate;
}
