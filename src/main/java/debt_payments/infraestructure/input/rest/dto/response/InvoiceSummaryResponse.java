package debt_payments.infraestructure.input.rest.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO que contiene un resumen de la información de una factura.
 * Se usará para enriquecer la respuesta del castigo de cartera.
 */
@Getter
@Setter
@Builder
public class InvoiceSummaryResponse {
    private Long id;
    private String factCode;
    private Long totalValue;
    private Long pendingValue; // El saldo pendiente ANTES del castigo
    private LocalDate expirationDate;
    private Long accountingAccount; // Nueva propiedad para el codigo de cuenta contable  
}
