package debt_payments.infraestructure.input.rest.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @brief DTO for response body when retrieving receipt detail information
 */

@Getter
@Setter
public class ReceiptDetailResponse {
    private Long invoiceId;           
    private BigDecimal amountPaid;   
    private String invoiceCode; 
    private Long accountingAccount;
}
