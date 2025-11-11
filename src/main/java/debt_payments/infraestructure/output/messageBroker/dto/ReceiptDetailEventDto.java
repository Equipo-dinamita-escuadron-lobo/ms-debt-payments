package debt_payments.infraestructure.output.messageBroker.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptDetailEventDto {
    private Long invoiceId;           
    private BigDecimal amountPaid;   
    private String invoiceCode; 
    private Long accountingAccount;
}
