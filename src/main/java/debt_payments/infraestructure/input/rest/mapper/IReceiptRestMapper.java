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
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ReceiptTypeMapper.class}) 
public interface IReceiptRestMapper {
    
    //Mapeo de DTO a Dominio
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "receiptCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "issueDate", ignore = true)
    @Mapping(target = "voidReasonDescription", ignore = true)
    @Mapping(target = "voidDate", ignore = true)
    @Mapping(source = "receiptTypeId", target = "receiptType")
    Receipt toDomain(ReceiptCreateRequest request);

    @Mapping(target = "id", ignore = true)
    ReceiptDetail toDomain(ReceiptDetailRequest detailRequest);

    //Mapeo de Dominio a DTO
    @Mapping(source = "status", target = "status")
    @Mapping(source = "paymentMethodId", target = "paymentMethodId")
    @Mapping(source = "issueDate", target = "issueDate")
    @Mapping(source = "receiptType.id", target = "receiptTypeId")
    ReceiptResponse toResponse(Receipt receipt);

    List<ReceiptResponse> toResponseList(List<Receipt> receipts);
/* 
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "receiptCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "issueDate", ignore = true)
    @Mapping(target = "voidReasonDescription", ignore = true)
    @Mapping(target = "voidDate", ignore = true)
    default Receipt toDomain(ReceiptCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        // Usamos el ID del tipo de recibo para decidir qué fábrica llamar
        // Asumiendo ReceiptType.INVOICE_PAYMENT.getId() es 1L
        if (Long.valueOf(1L).equals(request.getReceiptTypeId())) {
            List<ReceiptDetail> details = request.getDetails().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
            
            return Receipt.createForInvoicePayment(
                request.getEnterpriseId(),
                request.getThirdPartyId(),
                request.getPaymentMethodId(),
                request.getObservations(),
                details
            );
        } else {
            // Asumiendo el otro tipo es Ingreso Directo
            return Receipt.createForDirectIncome(
                request.getEnterpriseId(),
                request.getThirdPartyId(),
                request.getPaymentMethodId(),
                request.getObservations(),
                request.getTotalAmount(),
                request.getLedgerAccountId()
            );
        }
    }
        */
}
