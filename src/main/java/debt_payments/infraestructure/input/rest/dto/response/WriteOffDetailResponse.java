package debt_payments.infraestructure.input.rest.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO que representa un detalle de castigo en la respuesta, enriquecido con
 * información de la factura.
 */
@Getter
@Setter
@Builder
public class WriteOffDetailResponse {
    private Long amountWrittenOff; // El monto que fue castigado
    private InvoiceSummaryResponse invoice; // El resumen de la factura
}
