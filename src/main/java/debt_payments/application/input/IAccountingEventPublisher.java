package debt_payments.application.input;

import debt_payments.infraestructure.input.rest.dto.response.PortfolioWriteOffResponse;
import debt_payments.infraestructure.input.rest.dto.response.ReceiptResponse;

public interface IAccountingEventPublisher {
    /**
     * Publica un evento cuando un recibo de caja es creado.
     * @param receiptResponse El DTO del recibo de caja creado.
     */
    void publishReceiptCreatedEvent(ReceiptResponse receiptResponse);

    /**
     * Publica un evento cuando un recibo de caja es anulado.
     * @param receiptResponse El DTO del recibo de caja anulado.
     */
    void publishVoidReceiptEvent(ReceiptResponse receiptResponse);

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
