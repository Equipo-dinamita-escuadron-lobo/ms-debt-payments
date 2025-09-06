package debt_payments.application.input;

import java.util.List;
import java.util.Optional;

import debt_payments.domain.model.Receipt;

public interface IReceiptQueryUseCase {
    Optional<Receipt> findById(Long id);
    List<Receipt> findByInvoiceId(String invoiceId);
    List<Receipt> findByThirdPartyId(String thirdPartyId);
    List<Receipt> findByEnterpriseId(String enterpriseId);
}
