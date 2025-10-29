package debt_payments.infraestructure.input.rest.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateDueDateRequest {
    @NotNull(message = "La nueva fecha de vencimiento no puede ser nula.")
    @FutureOrPresent(message = "La nueva fecha de vencimiento no puede ser una fecha pasada.")
    private LocalDate newDueDate;
}
