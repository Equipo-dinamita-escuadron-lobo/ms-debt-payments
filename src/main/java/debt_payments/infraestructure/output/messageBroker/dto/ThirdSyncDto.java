package debt_payments.infraestructure.output.messageBroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdSyncDto {
    //TODO Revisar que los nombres de los campos que devuelve el entpoint original son estos
    private Long thirdPartyId;
    private String name;
    private String identificationNumber;
    private String enterpriseId;
    private boolean active;
}
