package debt_payments.application.input;

import debt_payments.infraestructure.input.rest.dto.response.ReceiptResponse;

public interface IAccountingEventPublisher {
    void publishReceiptCreatedEvent(ReceiptResponse receiptResponse);
    void publishVoidReceiptEvent(ReceiptResponse receiptResponse);
}
