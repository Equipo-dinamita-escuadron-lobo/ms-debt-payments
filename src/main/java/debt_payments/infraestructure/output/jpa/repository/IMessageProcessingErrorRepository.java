package debt_payments.infraestructure.output.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import debt_payments.infraestructure.output.jpa.entity.MessageProcessingErrorEntity;

/**
 * @brief Repository interface for managing Message Processing Error entities.
 * This interface extends JpaRepository to provide CRUD operations for MessageProcessingErrorEntity.
 */
public interface IMessageProcessingErrorRepository extends JpaRepository<MessageProcessingErrorEntity, Long> {
}
