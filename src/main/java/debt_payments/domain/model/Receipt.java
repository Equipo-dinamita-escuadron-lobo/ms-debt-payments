package debt_payments.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import debt_payments.domain.enums.ReceiptType;
import debt_payments.domain.exception.InvoiceNotFoundException;
import debt_payments.domain.model.Replica.InvoiceReplica;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class Receipt {
    private Long id;
    private String receiptCode;
    private String enterpriseId;
    private Long thirdPartyId;
    private Long paymentMethodId;
    private Long paymentMethodAccount;
    private ReceiptType receiptType;
    private ReceiptStatus status;
    private LocalDate issueDate;
    private Long totalAmount;
    private String observations;
    private String voidReasonDescription;
    private LocalDate voidDate;

    private Long ledgerAccountId; 
    private Long centerCostId;
    private List<ReceiptDetail> details;

    public boolean isInvoicePayment() {
        return this.receiptType != null && this.receiptType.getId().equals(ReceiptType.INVOICE_PAYMENT.getId());
    }

    /**
     * Method to create a receipt for invoice payment.
     * @param enterpriseId ID of the enterprise
     * @param thirdPartyId ID of the third party
     * @param paymentMethodId ID of the payment method
     * @param observations Observations for the receipt
     * @param details List of receipt details
     * @return A new Receipt instance configured for invoice payment
     */
    public static Receipt createForInvoicePayment(String enterpriseId, Long thirdPartyId, Long paymentMethodId, String observations, List<ReceiptDetail> details) {
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("Invoice payment receipt must have at least one detail.");
        }

        Receipt receipt = new Receipt();
        receipt.setReceiptType(ReceiptType.INVOICE_PAYMENT);
        receipt.enterpriseId = enterpriseId;
        receipt.thirdPartyId = thirdPartyId;
        receipt.paymentMethodId = paymentMethodId;
        receipt.observations = observations;
        receipt.details.addAll(details);
        receipt.status = ReceiptStatus.FINALIZED;
        receipt.issueDate = LocalDate.now();
        // El código y el total se calculan después, en el servicio o al persistir
        return receipt;
    }

    /**
     * Fabric to create a receipt for direct income.
     * @param enterpriseId ID of the enterprise
     * @param thirdPartyId ID of the third party
     * @param paymentMethodId ID of the payment method
     * @param observations Observations for the receipt
     * @param totalAmount Total amount of the receipt 
     * @param ledgerAccountId Account ledger ID
     * @return A new Receipt instance configured for direct income
     */
    public static Receipt createForDirectIncome(String enterpriseId, Long thirdPartyId, Long paymentMethodId, String observations, Long totalAmount, Long ledgerAccountId) {
        if (totalAmount == null || totalAmount <= 0) {
            throw new IllegalArgumentException("Direct income receipt must have a positive total amount.");
        }
        if (ledgerAccountId == null) {
            throw new IllegalArgumentException("Direct income receipt must specify a ledger account.");
        }

        Receipt receipt = new Receipt();
        receipt.setReceiptType(ReceiptType.DIRECT_INCOME);
        receipt.enterpriseId = enterpriseId;
        receipt.thirdPartyId = thirdPartyId;
        receipt.paymentMethodId = paymentMethodId;
        receipt.observations = observations;
        receipt.totalAmount = totalAmount;
        receipt.ledgerAccountId = ledgerAccountId;
        receipt.status = ReceiptStatus.FINALIZED;
        receipt.issueDate = LocalDate.now();
        
        return receipt;
    }

     /**
     * Apply payments from the receipt details to the corresponding invoices.
     * @param invoiceFinder A function that knows how to find an invoice by its ID.
     * @return A list of modified invoices that need to be persisted.
     * @throws Exception 
     */
    public List<InvoiceReplica> processInvoicePayments(Function<Long, Optional<InvoiceReplica>> invoiceFinder) throws Exception {
        if (this.receiptType != ReceiptType.INVOICE_PAYMENT) {
            return Collections.emptyList();
        }
        
        this.calculateTotalFromDetails(); // Asegura que el total sea correcto

        List<InvoiceReplica> modifiedInvoices = new ArrayList<>();
        for (ReceiptDetail detail : this.details) {
            InvoiceReplica invoice = invoiceFinder.apply(detail.getInvoiceId())
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice with id " + detail.getInvoiceId() + " not found."));
            
            // Delegamos la lógica de aplicar el pago a la factura
            invoice.applyPayment(detail.getAmountPaid());
            
            // Completamos el detalle con info de la factura
            detail.setInvoiceCode(invoice.getFactCode());
            detail.setAccountingAccount(invoice.getAccountingAccount());
            modifiedInvoices.add(invoice);
        }
        return modifiedInvoices;
    }

    /**
     * Void the receipt, reversing its effects on associated invoices if applicable.
     * @param reason The reason for voiding the receipt.
     * @param invoiceFinder A function that knows how to find an invoice by its ID.
     * @return A list of modified invoices that need to be persisted.
     * @throws Exception 
     */
    public List<InvoiceReplica> voidReceipt(String reason, Function<Long, Optional<InvoiceReplica>> invoiceFinder) throws Exception {
        if (this.status == ReceiptStatus.VOIDED) {
            throw new IllegalStateException("Receipt is already voided.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("A reason is required to void a receipt.");
        }
        this.status = ReceiptStatus.VOIDED;
        this.voidReasonDescription = reason;
        this.voidDate = LocalDate.now();
        
        if (this.receiptType != ReceiptType.INVOICE_PAYMENT) {
            return Collections.emptyList();
        }

        List<InvoiceReplica> modifiedInvoices = new ArrayList<>();
        for (ReceiptDetail detail : this.details) {
            InvoiceReplica invoice = invoiceFinder.apply(detail.getInvoiceId())
                .orElseThrow(() -> new InvoiceNotFoundException("Associated invoice with id " + detail.getInvoiceId() + " not found. Data might be inconsistent."));
            
            // Delegamos la lógica de revertir el pago a la factura
            invoice.reversePayment(detail.getAmountPaid());
            modifiedInvoices.add(invoice);
        }
        return modifiedInvoices;
    }

    public void assignReceiptCode(String code) {
        if (this.receiptCode != null) {
            throw new IllegalStateException("Receipt code is already assigned.");
        }
        this.receiptCode = code;
    }

    private void calculateTotalFromDetails() {
        if (this.receiptType == ReceiptType.INVOICE_PAYMENT) {
            this.totalAmount = this.details.stream()
                .mapToLong(ReceiptDetail::getAmountPaid)
                .sum();
        }
    }
}