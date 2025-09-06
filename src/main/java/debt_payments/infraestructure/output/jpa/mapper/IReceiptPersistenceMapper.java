package debt_payments.infraestructure.output.jpa.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import debt_payments.domain.model.Receipt;
import debt_payments.domain.model.ReceiptDetail;
import debt_payments.infraestructure.output.jpa.entity.ReceiptDetailEntity;
import debt_payments.infraestructure.output.jpa.entity.ReceiptEntity;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE) // Ignora advertencias si no todos los campos se mapean
public interface IReceiptPersistenceMapper {
    
     // --- Mapeo de Entidad (JPA) a Dominio ---

    Receipt toDomain(ReceiptEntity entity);
    List<Receipt> toDomainList(List<ReceiptEntity> entityList);
    ReceiptDetail toDomain(ReceiptDetailEntity detailEntity);

    ReceiptEntity toEntity(Receipt domain);
    ReceiptDetailEntity toEntity(ReceiptDetail detailDomain);
    List<ReceiptEntity> toEntityList(List<Receipt> domainList);

    @AfterMapping
    default void linkDetails(@MappingTarget ReceiptEntity entity) {
        if (entity.getDetails() != null) {
            entity.getDetails().forEach(detail -> detail.setReceipt(entity));
        }
    }
}
