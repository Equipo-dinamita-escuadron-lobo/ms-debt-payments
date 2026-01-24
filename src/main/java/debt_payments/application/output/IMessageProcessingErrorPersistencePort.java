package debt_payments.application.output;

import java.util.Optional;
import debt_payments.domain.model.MessageProcessingError;

public interface IMessageProcessingErrorPersistencePort {
    Optional<MessageProcessingError> findById(Long id);
    Optional<MessageProcessingError> findLastRecord();
    void deleteAll();
}
