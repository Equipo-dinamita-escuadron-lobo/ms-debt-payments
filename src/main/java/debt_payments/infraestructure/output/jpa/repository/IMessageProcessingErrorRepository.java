package debt_payments.infraestructure.output.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import debt_payments.infraestructure.output.jpa.entity.MessageProcessingErrorEntity;

/**
 * Repositorio para gestionar los errores de procesamiento de mensajes.
 */
public interface IMessageProcessingErrorRepository extends JpaRepository<MessageProcessingErrorEntity, Long> {
}
