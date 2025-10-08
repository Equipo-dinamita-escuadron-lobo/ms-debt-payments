package debt_payments.infraestructure.input.rest.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para el cuerpo de la petición (request body) al crear un nuevo registro de castigo de cartera.
 */
@Getter
@Setter
public class CreateWriteOffRequest {
    @NotBlank(message = "Justification is required.")
    @Size(max = 500, message = "Justification cannot exceed 500 characters.")
    private String justification;

    @NotNull(message = "Write-off date is required.")
    @FutureOrPresent(message = "Write-off date cannot be in the past.")
    private LocalDate writeOffDate;

    @NotNull(message = "Debit auxiliary account is required.")
    private Long debitAuxiliaryAccount;

    @NotNull(message = "Debit auxiliary account ID is required.")
    private Long debitAuxiliaryAccountId;

    @NotNull(message = "Third ID is required.")
    private Long thirdId;
    
    @NotBlank(message = "Enterprise ID is required.")
    private String enterpriseId;

    @NotEmpty(message = "At least one invoice detail must be provided.")
    @Valid 
    private List<WriteOffDetailRequest> details;
}
