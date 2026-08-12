package debt_payments.infraestructure.input.transaction;

import debt_payments.application.input.IReceiptCommandUseCase;
import debt_payments.application.input.IReceiptQueryUseCase;
import debt_payments.application.output.IAccountingEventPublisher;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.application.output.IReceiptCommandPersistencePort;
import debt_payments.application.output.IReceiptQueryPersistencePort;
import debt_payments.application.output.IResourceUsageNotifierPort;
import debt_payments.application.service.ReceiptService;
import debt_payments.domain.model.Receipt;
import debt_payments.infraestructure.output.audit.annotation.DocumentAuditable;
import debt_payments.infraestructure.output.audit.annotation.DocumentOperationType;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionalReceiptUseCase implements IReceiptCommandUseCase, IReceiptQueryUseCase {
    private final ReceiptService delegate;

    public TransactionalReceiptUseCase(IReceiptCommandPersistencePort commands,
            IReceiptQueryPersistencePort queries, IInvoiceProviderPort invoices,
            IAccountingEventPublisher accounting, IResourceUsageNotifierPort resources) {
        this.delegate = new ReceiptService(commands, queries, invoices, accounting, resources);
    }

    @Override
    @Transactional
    @DocumentAuditable(operationType = DocumentOperationType.CREATE, moduleName = "WALLET")
    public Receipt createReceipt(Receipt receipt) {
        return delegate.createReceipt(receipt);
    }

    @Override
    @Transactional
    @DocumentAuditable(operationType = DocumentOperationType.VOID, moduleName = "WALLET")
    public Receipt voidReceipt(Long receiptId, String reasonDescription) {
        return delegate.voidReceipt(receiptId, reasonDescription);
    }

    @Override
    @Transactional(readOnly = true)
    public Receipt findById(Long id) {
        return delegate.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receipt> findByInvoiceId(String invoiceId) {
        return delegate.findByInvoiceId(invoiceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receipt> findByThirdPartyId(String thirdPartyId, String enterpriseId) {
        return delegate.findByThirdPartyId(thirdPartyId, enterpriseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receipt> findByEnterpriseId(String enterpriseId) {
        return delegate.findByEnterpriseId(enterpriseId);
    }
}
