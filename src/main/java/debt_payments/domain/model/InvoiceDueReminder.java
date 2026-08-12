package debt_payments.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvoiceDueReminder {
    private Long thirdPartyId;
    private List<InvoiceDueReminderDetail> invoiceDetails;
}
