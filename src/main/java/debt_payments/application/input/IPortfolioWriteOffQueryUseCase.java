package debt_payments.application.input;

import java.util.List;
import java.util.Optional;

import debt_payments.domain.model.PortfolioWriteOff;
public interface IPortfolioWriteOffQueryUseCase {
    /**
     * @brief Find a portfolio write-off by its ID.
     * @param writeOffId The ID of the write-off to find.
     * @return An Optional of the domain model.
     */
    Optional<PortfolioWriteOff> findById(Long writeOffId);

    /**
     * @brief List all write-off records for a specific enterprise.
     * @param enterpriseId The ID of the enterprise.
     * @return A list of domain models.
     */
    List<PortfolioWriteOff> findByEnterpriseId(String enterpriseId);
}
