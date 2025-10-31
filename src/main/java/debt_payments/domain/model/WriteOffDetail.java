package debt_payments.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WriteOffDetail {
    private Long id;
    private Long invoiceId;
    private Long amountWrittenOff;
    private Long accountingAccount;
}
