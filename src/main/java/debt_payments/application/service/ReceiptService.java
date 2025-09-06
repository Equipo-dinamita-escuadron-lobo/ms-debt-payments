package debt_payments.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import debt_payments.application.input.IReceiptCommandUseCase;
import debt_payments.application.input.IReceiptQueryUseCase;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.application.output.IReceiptCommandPersistencePort;
import debt_payments.application.output.IReceiptQueryPersistencePort;
import debt_payments.application.output.IThirdPartyProviderPort;
import debt_payments.domain.model.Receipt;
import debt_payments.domain.model.ReceiptDetail;
import debt_payments.domain.model.ReceiptStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReceiptService implements IReceiptCommandUseCase, IReceiptQueryUseCase {

    private final IReceiptCommandPersistencePort receiptCommandPersistencePort;
    private final IReceiptQueryPersistencePort receiptQueryPersistencePort;
    private final IThirdPartyProviderPort thirdPartyProviderPort;
    private final IInvoiceProviderPort invoiceProviderPort;

    @Override
    @Transactional
    public Receipt createReceipt(Receipt receipt) {
        validateExternalDependencies(receipt);

        receipt.setReceiptCode(generateUniqueReceiptCode());
        receipt.setIssueDate(LocalDate.now());
        receipt.setStatus(ReceiptStatus.FINALIZED);

        return receiptCommandPersistencePort.save(receipt);
    }

    @Override
    @Transactional
    public Receipt voidReceipt(Long receiptId, String reasonDescription) {
        Receipt receiptToVoid = receiptQueryPersistencePort.findById(receiptId)
            .orElseThrow(() -> new IllegalArgumentException("Receipt with id " + receiptId + " does not exist."));

        if(receiptToVoid.getStatus() == ReceiptStatus.VOIDED)
            throw new IllegalStateException("Receipt with id " + receiptId + " is already voided.");

        receiptToVoid.setStatus(ReceiptStatus.VOIDED);
        receiptToVoid.setVoidReasonDescription(reasonDescription);
        receiptToVoid.setVoidDate(LocalDate.now());

        // TODO: Lógica de negocio para la anulación
        // - Generar un asiento contable de reversión.
        // - Restaurar los saldos de las facturas afectadas.

        return receiptCommandPersistencePort.save(receiptToVoid);
    }

    @Override
    @Transactional
    public Optional<Receipt> findById(Long id) {
        return receiptQueryPersistencePort.findById(id);
    }

    @Override
    @Transactional
    public List<Receipt> findByInvoiceId(String invoiceId) {
        return receiptQueryPersistencePort.findByInvoiceId(invoiceId);
    }

    @Override
    @Transactional
    public List<Receipt> findByThirdPartyId(String thirdPartyId) {
        return receiptQueryPersistencePort.findByThirdPartyId(thirdPartyId);
    }

    @Override
    @Transactional
    public List<Receipt> findByEnterpriseId(String enterpriseId) {
        return receiptQueryPersistencePort.findByEnterpriseId(enterpriseId);
    }

    private void validateExternalDependencies(Receipt receipt){
        if (!thirdPartyProviderPort.thirdPartyExists(receipt.getThirdPartyId())) {
            throw new IllegalArgumentException("Third Party with id " + receipt.getThirdPartyId() + " does not exist.");
        }

        // Si es un abono a factura, validar las facturas y los montos
        if(receipt.isInvoicePayment()){
            if(receipt.getDetails() == null || receipt.getDetails().isEmpty()){
                throw new IllegalArgumentException("Invoice payment details are required.");
            }

            for(ReceiptDetail detail: receipt.getDetails()){
                Long balance = invoiceProviderPort.getInvoiceBalance(detail.getInvoiceId()).
                orElseThrow(() -> new IllegalArgumentException("Invoice with id " + detail.getInvoiceId() + " does not exist."));

                if(detail.getAmountPaid().compareTo(balance) > 0){
                    throw new IllegalArgumentException("Amount paid for invoice " + detail.getInvoiceId() + " exceeds the invoice balance.");
                }
            }
            
        }
    }

    private String generateUniqueReceiptCode() {
        // Esto consultará un secuenciador de la base de datos.
        // Por ahora, un timestamp es suficiente para la demostración.
        return "RC-" + System.currentTimeMillis();
    }

}
