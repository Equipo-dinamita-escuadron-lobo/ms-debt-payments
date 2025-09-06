package debt_payments.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDetail {
    private Long id;
    private Long invoiceId;           
    private Long amountPaid;    
}
