package debt_payments.infraestructure.output.messageBroker.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceSyncDto {
    private Long factCode;
    private String entId;
    private Long thirdId;
    private Long totalValue;
    private Long totalPay;
    private Long pendingValue;
    private LocalDate creationDate;
    private LocalDate expirationDate;
    private boolean active;
    private Long accountingAccount;
}
