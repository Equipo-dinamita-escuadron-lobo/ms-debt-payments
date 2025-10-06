package debt_payments.domain.model;

import java.time.LocalDate;
import java.util.List;

import debt_payments.domain.enums.WriteOffStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PortfolioWriteOff {
    private Long id;
    private String justification;
    private LocalDate writeOffDate;
    private Long debitAuxiliaryAccount;
    private WriteOffStatus status;
    private String enterpriseId;
    private List<WriteOffDetail> details;

    /**
     * Confirma el castigo. Cambia el estado y valida la transición.
     */
    public void confirm() {
        if (this.status != WriteOffStatus.PENDING_CONFIRMATION) {
            throw new IllegalStateException("Only a write-off with PENDING_CONFIRMATION status can be confirmed.");
        }
        this.status = WriteOffStatus.CONFIRMED;
    }

    /**
     * Anula la confirmación del castigo. Cambia el estado y valida la transición.
     */
    public void voidConfirmation() {
        if (this.status != WriteOffStatus.CONFIRMED) {
            throw new IllegalStateException("Only a CONFIRMED write-off can be voided.");
        }
        this.status = WriteOffStatus.VOIDED;
    }
}
