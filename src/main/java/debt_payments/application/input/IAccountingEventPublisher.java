package debt_payments.application.input;

import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.domain.model.Receipt;

public interface IAccountingEventPublisher {
    /**
     * Publica un evento cuando un recibo de caja es creado.
     * @param receipt El recibo de caja creado.
     */
    void publishReceiptCreatedEvent(Receipt receipt);

    /**
     * Publica un evento cuando un recibo de caja es anulado.
     * @param receipt El recibo de caja anulado.
     */
    void publishVoidReceiptEvent(Receipt receipt);

    /**
     * Publica un evento cuando un castigo de cartera es confirmado.
     * @param writeOff El objeto de dominio del castigo de cartera confirmado.
     */
    void publishWriteOffConfirmedEvent(PortfolioWriteOff writeOff);

    /**
     * Publica un evento cuando un castigo de cartera es anulado.
     * @param writeOff El objeto de dominio del castigo de cartera anulado.
     */
    void publishWriteOffVoidedEvent(PortfolioWriteOff writeOff);
}
