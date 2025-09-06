package debt_payments.application.input;

import debt_payments.domain.model.Receipt;

public interface IReceiptCommandUseCase {

    Receipt createReceipt(Receipt receipt);
    Receipt voidReceipt(Long receiptId, String reasonDescription);
}
