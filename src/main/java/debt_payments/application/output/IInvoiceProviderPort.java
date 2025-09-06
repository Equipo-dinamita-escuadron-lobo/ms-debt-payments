package debt_payments.application.output;

import java.util.Optional;

public interface IInvoiceProviderPort {
    // Obtener el saldo pendiente de una factura por su ID
    Optional<Long> getInvoiceBalance(Long invoiceId);
}
