package debt_payments.infraestructure.input.rest.dto.response;

import java.time.LocalDate;
import java.util.List;

import debt_payments.domain.enums.WriteOffStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PortfolioWriteOffResponse {
    private Long id;
    private String justification;
    private Long totalAmount;
    private LocalDate writeOffDate;
    private Long debitAuxiliaryAccount;
    private Long debitAuxiliaryAccountId;
    private WriteOffStatus status;
    private String enterpriseId;
    private List<WriteOffDetailResponse> details; // Lista de detalles enriquecidos
}
