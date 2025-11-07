package debt_payments.infraestructure.output.messageBroker.dto;

import java.time.LocalDate;
import java.util.List;

import debt_payments.domain.enums.WriteOffStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PortfolioWriteOffEventDto {
    private Long id;
    private String code;
    private String justification;
    private Long totalAmount;
    private LocalDate writeOffDate;
    private Long debitAuxiliaryAccount;
    private Long debitAuxiliaryAccountId;
    private Long thirdId;
    private WriteOffStatus status;
    private String enterpriseId;
    private List<WriteOffDetailEventDto> details;
}
