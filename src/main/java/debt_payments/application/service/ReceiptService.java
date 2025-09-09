package debt_payments.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import debt_payments.application.input.IReceiptCommandUseCase;
import debt_payments.application.input.IReceiptQueryUseCase;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.application.output.IReceiptCommandPersistencePort;
import debt_payments.application.output.IReceiptQueryPersistencePort;
import debt_payments.domain.exception.InvoiceNotFoundException;
import debt_payments.domain.exception.ReceiptNotFoundException;
import debt_payments.domain.model.Receipt;
import debt_payments.domain.model.ReceiptDetail;
import debt_payments.domain.model.ReceiptStatus;
import debt_payments.domain.model.Replica.InvoiceReplica;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReceiptService implements IReceiptCommandUseCase, IReceiptQueryUseCase {

    private final IReceiptCommandPersistencePort receiptCommandPersistencePort;
    private final IReceiptQueryPersistencePort receiptQueryPersistencePort;
    private final IInvoiceProviderPort invoiceProviderPort;

    /**
     * Creates a new receipt after validating external dependencies and generating a unique receipt code.
     * @param receipt The receipt to be created.
     * @return The created receipt with updated fields.
     */
    @Override
    public Receipt createReceipt(Receipt receipt) {
        // validateThirdParty(receipt.getThirdPartyId()); // Validar tercero (desactivado temporalmente)

        Long totalAmount = 0L; // Inicializar total con Long

        if (receipt.isInvoicePayment()) {
            if (receipt.getDetails() == null || receipt.getDetails().isEmpty()) {
                throw new IllegalArgumentException("Invoice payment details are required.");
            }
            
            for (ReceiptDetail detail : receipt.getDetails()) {
                // 1. Buscar la factura
                InvoiceReplica invoice = invoiceProviderPort.findInvoiceById(detail.getInvoiceId())
                    .orElseThrow(() -> new IllegalArgumentException("Invoice with id " + detail.getInvoiceId() + " does not exist."));

                // 2. Validar que el pago no exceda el saldo
                if (detail.getAmountPaid() > invoice.getPendingValue()) {
                    throw new IllegalArgumentException("Amount paid for invoice " + invoice.getFactCode() + " exceeds the pending balance.");
                }

                // 3. Actualizar el saldo de la factura
                invoice.setPendingValue(invoice.getPendingValue() - detail.getAmountPaid());
                invoice.setTotalPay(invoice.getTotalPay() + detail.getAmountPaid());
                invoiceProviderPort.updateInvoice(invoice);

                // 4. Guardar el código de la factura en el detalle (Tu petición)
                detail.setInvoiceCode(invoice.getFactCode());
                detail.setAccountingAccount(invoice.getAccountingAccount());
                
                
                // 5. Sumar al total del recibo
                totalAmount += detail.getAmountPaid();
            }
        }else{
            totalAmount = receipt.getTotalAmount();
        }

        // 6. Completar y guardar el recibo
        //Para guardar monto total dependiendo del tipo de recibo
        if(receipt.isInvoicePayment()){
            receipt.setTotalAmount(totalAmount);
        }else{
            receipt.setTotalAmount(receipt.getTotalAmount());
        }
        
        receipt.setReceiptCode(generateUniqueReceiptCode());
        receipt.setIssueDate(LocalDate.now());
        receipt.setStatus(ReceiptStatus.FINALIZED);
        

        Receipt savedReceipt = receiptCommandPersistencePort.save(receipt);

        return savedReceipt;
    }

    @Override
    public Receipt voidReceipt(Long receiptId, String reasonDescription) {
        Receipt receiptToVoid = receiptQueryPersistencePort.findById(receiptId)
            .orElseThrow(() -> new ReceiptNotFoundException("Receipt with id " + receiptId + " does not exist."));

        if (receiptToVoid.getStatus() == ReceiptStatus.VOIDED) {
            throw new IllegalStateException("Receipt with id " + receiptId + " is already voided.");
        }

        // 1. Revertir los pagos en las facturas afectadas
        if (receiptToVoid.isInvoicePayment()) {
            for (ReceiptDetail detail : receiptToVoid.getDetails()) {
                InvoiceReplica invoice = invoiceProviderPort.findInvoiceById(detail.getInvoiceId())
                    .orElseThrow(() -> new InvoiceNotFoundException("Associated invoice with id " + detail.getInvoiceId() + " not found. Data might be inconsistent."));

                // Revertir el saldo de la factura
                invoice.setPendingValue(invoice.getPendingValue() + detail.getAmountPaid());
                invoice.setTotalPay(invoice.getTotalPay() - detail.getAmountPaid());
                invoiceProviderPort.updateInvoice(invoice);
            }
        }
        
        // 2. Actualizar el estado del recibo
        receiptToVoid.setStatus(ReceiptStatus.VOIDED);
        receiptToVoid.setVoidReasonDescription(reasonDescription);
        receiptToVoid.setVoidDate(LocalDate.now());

        return receiptCommandPersistencePort.save(receiptToVoid);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Receipt> findById(Long id) {
        return receiptQueryPersistencePort.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receipt> findByInvoiceId(String invoiceId) {
        return receiptQueryPersistencePort.findByInvoiceId(invoiceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receipt> findByThirdPartyId(String thirdPartyId) {
        return receiptQueryPersistencePort.findByThirdPartyId(thirdPartyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receipt> findByEnterpriseId(String enterpriseId) {
        return receiptQueryPersistencePort.findByEnterpriseId(enterpriseId);
    }

    private String generateUniqueReceiptCode() {
        // Esto consultará un secuenciador de la base de datos.
        // Por ahora, un timestamp es suficiente para la demostración.
        return "RC-" + System.currentTimeMillis();
    }

}
