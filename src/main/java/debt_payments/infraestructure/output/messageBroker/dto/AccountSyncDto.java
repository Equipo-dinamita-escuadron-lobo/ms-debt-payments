package debt_payments.infraestructure.output.messageBroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountSyncDto {
    private Long accountId;
    private String accountNumber;
    private String accountName;
    private String accountType;
    private boolean active;
    private Long enterpriseId;
}
