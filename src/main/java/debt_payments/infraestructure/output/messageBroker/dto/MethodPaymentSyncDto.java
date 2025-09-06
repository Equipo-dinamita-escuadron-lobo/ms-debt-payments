package debt_payments.infraestructure.output.messageBroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MethodPaymentSyncDto {
    private Long methodPaymentId;
    private String name;
    private String type;
    private boolean active;
    private Long enterpriseId;
}
