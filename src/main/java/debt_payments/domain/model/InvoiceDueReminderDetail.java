package debt_payments.domain.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvoiceDueReminderDetail {
    private Long invoiceId;
    private Long invoiceCode;
    private LocalDate expirationDate;
    private Long totalAmount;
    private Long pendingValue;
}
