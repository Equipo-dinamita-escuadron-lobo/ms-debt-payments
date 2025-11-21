package debt_payments.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import debt_payments.domain.enums.WriteOffStatus;
import debt_payments.domain.exception.InvoiceNotFoundException;
import debt_payments.domain.model.Replica.InvoiceReplica;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PortfolioWriteOff {
    private Long id;
    private String code;
    private String justification;
    private Long totalAmount;
    private LocalDate writeOffDate;
    private Long debitAuxiliaryAccount;
    private Long debitAuxiliaryAccountId;
    private Long thirdId;
    private Long costCenterId;
    private WriteOffStatus status;
    private String enterpriseId;
    private List<WriteOffDetail> details;

    public static PortfolioWriteOff create(String enterpriseId, Long thirdId, String justification,
            List<WriteOffDetail> details, Long costCenterId) {
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("A write-off must have at least one invoice detail.");
        }
        if (justification == null || justification.isBlank()) {
            throw new IllegalArgumentException("A justification is required for a write-off.");
        }

        PortfolioWriteOff writeOff = new PortfolioWriteOff();
        writeOff.enterpriseId = enterpriseId;
        writeOff.thirdId = thirdId;
        writeOff.justification = justification;
        writeOff.details = details;
        writeOff.status = WriteOffStatus.PENDING_CONFIRMATION; // Estado inicial por defecto
        writeOff.writeOffDate = LocalDate.now();
        writeOff.costCenterId = costCenterId;

        // El total debería calcularse, no asignarse
        writeOff.calculateTotalAmount();

        return writeOff;
    }

    private void calculateTotalAmount() {
        if (this.details != null) {
            this.totalAmount = this.details.stream()
                    .mapToLong(WriteOffDetail::getAmountWrittenOff)
                    .sum();
        }
    }

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

    /**
     * Prepara las facturas para el castigo, marcándolas como pendientes.
     * @return Lista de facturas modificadas para persistir.
     */
    public List<InvoiceReplica> prepareInvoicesForWriteOff(Function<Long, Optional<InvoiceReplica>> invoiceFinder) {
        List<InvoiceReplica> modifiedInvoices = new ArrayList<>();
        for (WriteOffDetail detail : this.details) {
            InvoiceReplica invoice = invoiceFinder.apply(detail.getInvoiceId())
                    .orElseThrow(() -> new InvoiceNotFoundException("Invoice " + detail.getInvoiceId() + " not found."));
            
            // Aquí el dominio se auto-completa
            detail.setAmountWrittenOff(invoice.getPendingValue());
            detail.setAccountingAccount(invoice.getAccountingAccount());
            
            invoice.markAsPendingWriteOff(); // Nuevo método en InvoiceReplica
            modifiedInvoices.add(invoice);
        }
        this.calculateTotalAmount(); // Recalcular total con los valores reales
        return modifiedInvoices;
    }

    /**
     * Ejecuta el castigo final sobre las facturas.
     * @return Lista de facturas modificadas para persistir.
     */
    public List<InvoiceReplica> processConfirmation(Function<Long, Optional<InvoiceReplica>> invoiceFinder) {
        if (this.status != WriteOffStatus.CONFIRMED) {
            throw new IllegalStateException("Write-off must be CONFIRMED to process.");
        }
        List<InvoiceReplica> modifiedInvoices = new ArrayList<>();
        for (WriteOffDetail detail : this.details) {
            InvoiceReplica invoice = invoiceFinder.apply(detail.getInvoiceId())
                    .orElseThrow(() -> new InvoiceNotFoundException("Invoice " + detail.getInvoiceId() + " not found."));
            invoice.writeOff();
            modifiedInvoices.add(invoice);
        }
        return modifiedInvoices;
    }

    /**
     * Revierte el castigo en las facturas.
     * @return Lista de facturas modificadas para persistir.
     */
    public List<InvoiceReplica> processVoidance(Function<Long, Optional<InvoiceReplica>> invoiceFinder) {
        if (this.status != WriteOffStatus.VOIDED) {
            throw new IllegalStateException("Write-off must be VOIDED to process voidance.");
        }
        List<InvoiceReplica> modifiedInvoices = new ArrayList<>();
        for (WriteOffDetail detail : this.details) {
            InvoiceReplica invoice = invoiceFinder.apply(detail.getInvoiceId())
                    .orElseThrow(() -> new InvoiceNotFoundException("Invoice " + detail.getInvoiceId() + " not found."));
            invoice.reverseWriteOff(detail.getAmountWrittenOff()); // Nuevo método en InvoiceReplica
            modifiedInvoices.add(invoice);
        }
        return modifiedInvoices;
    }
}
