package debt_payments.application.service;

import debt_payments.application.input.IMessageProcessingErrorCommandPort;
import debt_payments.application.input.IMessageProcessingErrorQueryPort;
import debt_payments.application.output.IMessageProcessingErrorPersistencePort;
import debt_payments.domain.model.MessageProcessingError;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageProcessingErrorService implements IMessageProcessingErrorCommandPort, IMessageProcessingErrorQueryPort{
    
    private final IMessageProcessingErrorPersistencePort persistencePort;
    
    @Override
    public MessageProcessingError findById(Long id) {
        return persistencePort.findById(id).orElseThrow(() -> 
            new debt_payments.domain.exception.MessageProcessingErrorNotFoundException("MessageProcessingError with ID " + id + " not found"));
    }

    @Override
    public MessageProcessingError findLastRecord() {
        return persistencePort.findLastRecord().orElseThrow(() -> 
            new debt_payments.domain.exception.MessageProcessingErrorNotFoundException("No message processing errors found"));
    }

    @Override
    public void deleteAll() {
        persistencePort.deleteAll();
    }
    
}
