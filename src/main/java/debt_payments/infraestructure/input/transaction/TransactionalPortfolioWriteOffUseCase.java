package debt_payments.infraestructure.input.transaction;

import debt_payments.application.input.IPortfolioWriteOffCommandUseCase;
import debt_payments.application.input.IPortfolioWriteOffQueryUseCase;
import debt_payments.application.output.IAccountingEventPublisher;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.application.output.IPortfolioWriteOffPersistencePort;
import debt_payments.application.output.IResourceUsageNotifierPort;
import debt_payments.application.service.PortfolioWriteOffService;
import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.infraestructure.output.audit.annotation.DocumentAuditable;
import debt_payments.infraestructure.output.audit.annotation.DocumentOperationType;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionalPortfolioWriteOffUseCase
        implements IPortfolioWriteOffCommandUseCase, IPortfolioWriteOffQueryUseCase {
    private final PortfolioWriteOffService delegate;

    public TransactionalPortfolioWriteOffUseCase(IPortfolioWriteOffPersistencePort writeOffs,
            IInvoiceProviderPort invoices, IAccountingEventPublisher accounting,
            IResourceUsageNotifierPort resources) {
        this.delegate = new PortfolioWriteOffService(writeOffs, invoices, accounting, resources);
    }

    @Override
    @Transactional
    @DocumentAuditable(operationType = DocumentOperationType.CREATE, moduleName = "WALLET")
    public PortfolioWriteOff createWriteOff(PortfolioWriteOff writeOff) {
        return delegate.createWriteOff(writeOff);
    }

    @Override
    @Transactional
    @DocumentAuditable(operationType = DocumentOperationType.APPROVE, moduleName = "WALLET")
    public PortfolioWriteOff confirmWriteOff(Long writeOffId) {
        return delegate.confirmWriteOff(writeOffId);
    }

    @Override
    @Transactional
    @DocumentAuditable(operationType = DocumentOperationType.VOID, moduleName = "WALLET")
    public PortfolioWriteOff voidWriteOffConfirmation(Long writeOffId) {
        return delegate.voidWriteOffConfirmation(writeOffId);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioWriteOff findById(Long writeOffId) {
        return delegate.findById(writeOffId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioWriteOff> findByEnterpriseId(String enterpriseId) {
        return delegate.findByEnterpriseId(enterpriseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioWriteOff> findConfirmedOrVoidedByEnterpriseId(String enterpriseId) {
        return delegate.findConfirmedOrVoidedByEnterpriseId(enterpriseId);
    }
}
