package debt_payments.infraestructure.output.messageBroker.mapper;

import java.math.BigDecimal;
import java.util.List;

import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.infraestructure.output.jpa.entity.replicas.InvoiceReplicaEntity;

public interface IInvoicePersistenceMapper {
    //Metodo para pasar a domain
    BigDecimal toDomain(BigDecimal balance);

    //Metodo para pasar listado de entidades a listado de domain
    List<InvoiceReplica> toInvoiceReplicaList(List<InvoiceReplicaEntity> invoiceEntityList);
}
