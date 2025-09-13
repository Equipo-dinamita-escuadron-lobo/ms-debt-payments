package debt_payments.infraestructure.input.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import debt_payments.domain.model.Receipt;
import debt_payments.domain.model.ReceiptDetail;
import debt_payments.infraestructure.input.rest.dto.request.ReceiptCreateRequest;
import debt_payments.infraestructure.input.rest.dto.request.ReceiptDetailRequest;
import debt_payments.infraestructure.input.rest.dto.response.ReceiptResponse;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE) 
public interface IReceiptRestMapper {
    
    //Mapeo de DTO a Dominio
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "receiptCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "issueDate", ignore = true)
    @Mapping(target = "voidReasonDescription", ignore = true)
    @Mapping(target = "voidDate", ignore = true)
    Receipt toDomain(ReceiptCreateRequest request);

    @Mapping(target = "id", ignore = true)
    ReceiptDetail toDomain(ReceiptDetailRequest detailRequest);

    //Mapeo de Dominio a DTO
    @Mapping(source = "status", target = "status")
    @Mapping(source = "paymentMethodId", target = "paymentMethodId")
    @Mapping(source = "issueDate", target = "issueDate")
    ReceiptResponse toResponse(Receipt receipt);

    List<ReceiptResponse> toResponseList(List<Receipt> receipts);
}
