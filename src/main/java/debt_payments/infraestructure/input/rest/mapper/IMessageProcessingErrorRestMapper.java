package debt_payments.infraestructure.input.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import debt_payments.domain.model.MessageProcessingError;
import debt_payments.infraestructure.input.rest.dto.response.MessageProcessingErrorResponse;


@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IMessageProcessingErrorRestMapper {

    MessageProcessingErrorResponse toResponse(MessageProcessingError domain);

}
