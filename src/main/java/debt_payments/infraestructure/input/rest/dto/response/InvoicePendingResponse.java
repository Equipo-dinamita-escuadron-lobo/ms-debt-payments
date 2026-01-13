package debt_payments.infraestructure.input.rest.dto.response;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * @brief DTO for response body when retrieving pending invoice information 
 */

@Getter
@Setter
public class InvoicePendingResponse {
    private Long id;
    private String factCode;
    private Long pendingValue;
    private Long thirdId;
    private Long totalValue;
    private LocalDate creationDate;
    private LocalDate expirationDate;
}
