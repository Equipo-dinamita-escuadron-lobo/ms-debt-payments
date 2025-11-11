package debt_payments.infraestructure.output.messageBroker.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import debt_payments.domain.model.Receipt;
import debt_payments.domain.model.ReceiptDetail;
import debt_payments.infraestructure.output.messageBroker.dto.ReceiptDetailEventDto;
import debt_payments.infraestructure.output.messageBroker.dto.ReceiptEventDto;

@Mapper(componentModel = "spring")
public interface IReceiptEventMapper {
    @Mapping(target = "id", ignore = true)
    ReceiptDetail toEventDtoDetail(ReceiptDetailEventDto detailRequest);

    //Mapeo de Dominio a DTO
    @Mapping(source = "status", target = "status")
    @Mapping(source = "paymentMethodId", target = "paymentMethodId")
    @Mapping(source = "issueDate", target = "issueDate")
    @Mapping(source = "receiptType.id", target = "receiptTypeId")
    ReceiptEventDto toEventDto(Receipt receipt);

    List<ReceiptEventDto> toEventDtoList(List<Receipt> receipts);

}
