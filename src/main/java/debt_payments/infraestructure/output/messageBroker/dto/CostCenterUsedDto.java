package debt_payments.infraestructure.output.messageBroker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CostCenterUsedDto {
    private Long costCenterId;
    private String enterpriseId;
}
