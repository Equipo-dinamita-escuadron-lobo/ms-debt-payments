package debt_payments.application.output;

import java.util.List;
import java.util.Optional;

import debt_payments.domain.model.Receipt;

public interface IReceiptQueryPersistencePort {
    Optional<Receipt> findById(Long id);
    List<Receipt> findByInvoiceId(String invoiceId);
    List<Receipt> findByThirdPartyId(String thirdPartyId);
    List<Receipt> findByEnterpriseId(String enterpriseId);
    boolean existsById(Long id);
}
