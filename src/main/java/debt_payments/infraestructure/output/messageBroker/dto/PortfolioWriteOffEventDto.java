package debt_payments.infraestructure.output.messageBroker.dto;

import java.time.LocalDate;
import java.util.List;

import debt_payments.domain.enums.WriteOffStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
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
    private Long centerCostId;
    private List<WriteOffDetailEventDto> details;
}
