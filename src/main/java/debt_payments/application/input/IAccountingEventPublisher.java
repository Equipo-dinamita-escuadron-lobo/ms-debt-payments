package debt_payments.application.input;

import debt_payments.domain.model.Receipt;
import debt_payments.infraestructure.input.rest.dto.response.PortfolioWriteOffResponse;

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
     * @param writeOffResponse El DTO del castigo de cartera confirmado.
     */
    void publishWriteOffConfirmedEvent(PortfolioWriteOffResponse writeOffResponse);

    /**
     * Publica un evento cuando un castigo de cartera es anulado.
     * @param writeOffResponse El DTO del castigo de cartera anulado.
     */
    void publishWriteOffVoidedEvent(PortfolioWriteOffResponse writeOffResponse);
}
