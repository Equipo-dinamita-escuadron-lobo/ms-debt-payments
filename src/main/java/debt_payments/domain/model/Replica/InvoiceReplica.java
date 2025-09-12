package debt_payments.domain.model.Replica;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceReplica {
    private Long id;
    private String factCode; 
    private Long totalValue;
    private Long totalPay;
    private Long thirdId;
    private Long pendingValue;
    private Long accountingAccount;
    private LocalDate expirationDate;
}
