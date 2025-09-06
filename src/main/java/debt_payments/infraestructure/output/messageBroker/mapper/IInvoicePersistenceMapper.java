package debt_payments.infraestructure.output.messageBroker.mapper;

import java.math.BigDecimal;

public interface IInvoicePersistenceMapper {
    //Metodo para pasar a domain
    BigDecimal toDomain(BigDecimal balance);
}
