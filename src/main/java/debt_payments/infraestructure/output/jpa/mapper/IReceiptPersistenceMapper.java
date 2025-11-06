package debt_payments.infraestructure.output.jpa.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import debt_payments.domain.enums.ReceiptType;
import debt_payments.domain.model.Receipt;
import debt_payments.domain.model.ReceiptDetail;
import debt_payments.infraestructure.output.jpa.entity.ReceiptDetailEntity;
import debt_payments.infraestructure.output.jpa.entity.ReceiptEntity;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE) // Ignora advertencias si no todos los campos se mapean
public interface IReceiptPersistenceMapper {

    @Mapping(source = "receiptTypeId", target = "receiptType")
    Receipt toDomain(ReceiptEntity entity);
    List<Receipt> toDomainList(List<ReceiptEntity> entityList);
    ReceiptDetail toDomain(ReceiptDetailEntity detailEntity);

    @Mapping(source = "receiptType.id", target = "receiptTypeId")
    ReceiptEntity toEntity(Receipt domain);
    ReceiptDetailEntity toEntity(ReceiptDetail detailDomain);
    List<ReceiptEntity> toEntityList(List<Receipt> domainList);

    /**
     * MapStruct cannot map a Long to a ReceiptType automatically, so provide a helper method that resolves
     * a ReceiptType instance from its id; MapStruct will pick this up and use it when mapping receiptTypeId -> receiptType.
     */
    default ReceiptType map(Long receiptTypeId) {
        if (receiptTypeId == null) {
            return null;
        }
        // Try to match by a likely 'id' property or getter using reflection, then fall back to ordinal.
        for (ReceiptType candidate : ReceiptType.values()) {
            try {
                // Try getter method 'getId'
                try {
                    java.lang.reflect.Method m = candidate.getClass().getMethod("getId");
                    Object val = m.invoke(candidate);
                    if (val instanceof Number && ((Number) val).longValue() == receiptTypeId.longValue()) {
                        return candidate;
                    }
                } catch (NoSuchMethodException ignored) {
                    // try direct field 'id'
                    try {
                        java.lang.reflect.Field f = candidate.getClass().getDeclaredField("id");
                        f.setAccessible(true);
                        Object val = f.get(candidate);
                        if (val instanceof Number && ((Number) val).longValue() == receiptTypeId.longValue()) {
                            return candidate;
                        }
                    } catch (NoSuchFieldException | IllegalAccessException ignored2) {
                        // continue to next candidate
                    }
                }
            } catch (Exception e) {
                // ignore and continue
            }
        }
        // As a last resort, try matching by ordinal (if ids were stored as ordinals)
        ReceiptType[] values = ReceiptType.values();
        if (receiptTypeId.longValue() >= 0 && receiptTypeId.longValue() < values.length) {
            return values[(int) receiptTypeId.longValue()];
        }
        return null;
    }

    /**
     * Esta anotación le dice a MapStruct que ejecute este método DESPUÉS de haber mapeadoun objeto Receipt a un ReceiptEntity.
     * @param receiptEntity El objeto de destino (@MappingTarget) que acaba de ser creado.
     * 
     * El método itera sobre cada detalle (hijo) y le asigna una referencia a su recibo (padre).
     * Esto asegura que cuando JPA intente guardar, el vínculo bidireccional esté completoy la columna de la clave foránea 'receipt_id' se rellene correctamente.
     */
    @AfterMapping
    default void linkDetails(@MappingTarget ReceiptEntity entity) {
        if (entity.getDetails() != null) {
            entity.getDetails().forEach(detail -> detail.setReceipt(entity));
        }
    }
}
