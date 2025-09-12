package debt_payments.infraestructure.output.jpa.mapper.replicas;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.infraestructure.output.jpa.entity.replicas.InvoiceReplicaEntity;


@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IInvoicePersistenceMapper {
    /**
     * Convierte una entidad JPA (InvoiceReplicaEntity) a un objeto de dominio (InvoiceReplica).
     * La lógica de negocio solo trabajará con objetos de dominio.
     */
    InvoiceReplica toDomain(InvoiceReplicaEntity entity);

    /**
     * Convierte un objeto de dominio (InvoiceReplica) a una entidad JPA (InvoiceReplicaEntity).
     * Esto es necesario antes de poder guardar los cambios en la base de datos.
     */
    
    InvoiceReplicaEntity toEntity(InvoiceReplica domain);

    /**
     * Convierte una lista de entidades JPA a una lista de objetos de dominio.
     * @param invoiceEntityList La lista de entidades JPA a convertir.
     * @return La lista de objetos de dominio resultante.
     */
    List<InvoiceReplica> toInvoiceReplicaList(List<InvoiceReplicaEntity> invoiceEntityList);
}
