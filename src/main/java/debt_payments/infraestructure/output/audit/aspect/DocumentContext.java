package debt_payments.infraestructure.output.audit.aspect;

import java.time.LocalDate;

public record DocumentContext(
        String documentId,
        String documentCode,
        String enterpriseId,
        String documentType,
        LocalDate documentDate,
        String thirdPartyId,
        String thirdPartyName) {
}