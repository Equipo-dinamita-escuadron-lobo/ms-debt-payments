package debt_payments.infraestructure.output.messageBroker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.infraestructure.output.messageBroker.dto.PortfolioWriteOffEventDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IPortfolioWriteOffEventMapper {

    /**
     * Map a object of PortfolioWriteOff to PortfolioWriteOffEventDto
     * @param domain the PortfolioWriteOff domain object
     * @return the mapped PortfolioWriteOffEventDto object
     */
    @Mapping(target = "details", ignore = true)
    PortfolioWriteOffEventDto toEventDto(PortfolioWriteOff domain);
}
