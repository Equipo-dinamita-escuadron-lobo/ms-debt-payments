package debt_payments.infraestructure.input.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.infraestructure.input.rest.dto.response.InvoicePendingResponse;
import debt_payments.infraestructure.input.rest.dto.response.InvoiceSummaryResponse;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IInvoiceRestMapper {
    @Mapping(source = "id", target = "id")
    InvoicePendingResponse toInvoicePendingResponse(InvoiceReplica invoiceReplica);

    List<InvoicePendingResponse> toInvoicePendingResponseList(List<InvoiceReplica> invoiceReplicaList);

    InvoiceSummaryResponse toSummaryResponse(InvoiceReplica invoiceReplica);

    List<InvoiceSummaryResponse> toSummaryResponseList(List<InvoiceReplica> invoiceReplicaList);
}
