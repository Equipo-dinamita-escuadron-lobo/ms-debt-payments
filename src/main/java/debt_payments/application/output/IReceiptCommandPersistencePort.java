package debt_payments.application.output;

import debt_payments.domain.model.Receipt;

public interface IReceiptCommandPersistencePort {
    Receipt save(Receipt receipt);
}
