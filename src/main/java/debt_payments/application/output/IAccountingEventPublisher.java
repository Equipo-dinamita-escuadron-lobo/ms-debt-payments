package debt_payments.application.output;

import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.domain.model.Receipt;

public interface IAccountingEventPublisher {
    void publishReceiptCreatedEvent(Receipt receipt);
    void publishVoidReceiptEvent(Receipt receipt);
    void publishWriteOffConfirmedEvent(PortfolioWriteOff writeOff);
    void publishWriteOffVoidedEvent(PortfolioWriteOff writeOff);
}
