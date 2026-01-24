package debt_payments.infraestructure.output.jpa.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import debt_payments.application.output.IMessageProcessingErrorPersistencePort;
import debt_payments.domain.model.MessageProcessingError;
import debt_payments.infraestructure.output.jpa.mapper.IMessageProcessingErrorPersistenceMapper;
import debt_payments.infraestructure.output.jpa.repository.IMessageProcessingErrorRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageProcessingErrorPersistenceAdapter implements IMessageProcessingErrorPersistencePort {

    private final IMessageProcessingErrorRepository repository;
    private final IMessageProcessingErrorPersistenceMapper mapper;

    @Override
    public Optional<MessageProcessingError> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<MessageProcessingError> findLastRecord() {
        return repository.findFirstByOrderByIdDesc().map(mapper::toDomain);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
    
}
