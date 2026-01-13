package debt_payments.infraestructure.output.messageBroker.mapper;

import java.math.BigDecimal;
import java.util.List;

import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.infraestructure.output.jpa.entity.replicas.InvoiceReplicaEntity;


/**
 * @brief Mapper interface for converting between domain models and persistence entities related to invoices.
 */

public interface IInvoicePersistenceMapper {

    BigDecimal toDomain(BigDecimal balance);

    List<InvoiceReplica> toInvoiceReplicaList(List<InvoiceReplicaEntity> invoiceEntityList);
}
