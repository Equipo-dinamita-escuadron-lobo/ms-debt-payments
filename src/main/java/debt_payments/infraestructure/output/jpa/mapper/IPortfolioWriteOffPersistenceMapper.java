package debt_payments.infraestructure.output.jpa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.domain.model.WriteOffDetail;
import debt_payments.infraestructure.output.jpa.entity.PortfolioWriteOffEntity;
import debt_payments.infraestructure.output.jpa.entity.WriteOffDetailEntity;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IPortfolioWriteOffPersistenceMapper {
    // --- Mapeo de Cabecera (PortfolioWriteOff) ---
    PortfolioWriteOffEntity toEntity(PortfolioWriteOff domain);
    PortfolioWriteOff toDomain(PortfolioWriteOffEntity entity);

    // --- Mapeo de Detalle (WriteOffDetail) ---
    WriteOffDetailEntity toEntity(WriteOffDetail domain);
    WriteOffDetail toDomain(WriteOffDetailEntity entity);

    // MapStruct se encarga automáticamente de las listas
    List<PortfolioWriteOff> toDomainList(List<PortfolioWriteOffEntity> entityList);
}
