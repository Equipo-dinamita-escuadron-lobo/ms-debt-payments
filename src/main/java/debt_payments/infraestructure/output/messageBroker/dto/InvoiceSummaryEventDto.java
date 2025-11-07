package debt_payments.infraestructure.output.messageBroker.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class InvoiceSummaryEventDto {
    private Long id;
    private String factCode;
    private Long totalValue;
    private Long pendingValue;
    private LocalDate expirationDate;
    private Long accountingAccount;
}
