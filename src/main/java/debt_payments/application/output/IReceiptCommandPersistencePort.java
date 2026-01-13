package debt_payments.application.output;

import debt_payments.domain.model.Receipt;

public interface IReceiptCommandPersistencePort {
    /**
     * @brief Save a new receipt record.
     * @param receipt The domain model to persist.
     * @return The persisted domain model (with the assigned ID).
     */
    Receipt save(Receipt receipt);
}
