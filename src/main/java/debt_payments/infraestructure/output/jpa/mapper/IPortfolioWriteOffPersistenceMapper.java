package debt_payments.infraestructure.output.jpa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.domain.model.WriteOffDetail;
import debt_payments.infraestructure.output.jpa.entity.PortfolioWriteOffEntity;
import debt_payments.infraestructure.output.jpa.entity.WriteOffDetailEntity;

/**
 * Mapper interface for converting between Portfolio Write-Off domain models and Portfolio Write-Off JPA entities.
 * Utilizes MapStruct for automatic generation of mapping implementations.
 */

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IPortfolioWriteOffPersistenceMapper {

    PortfolioWriteOffEntity toEntity(PortfolioWriteOff domain);
    PortfolioWriteOff toDomain(PortfolioWriteOffEntity entity);

    WriteOffDetailEntity toEntity(WriteOffDetail domain);
    WriteOffDetail toDomain(WriteOffDetailEntity entity);

    List<PortfolioWriteOff> toDomainList(List<PortfolioWriteOffEntity> entityList);
}
