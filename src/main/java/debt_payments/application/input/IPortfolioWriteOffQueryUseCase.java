package debt_payments.application.input;

import java.util.List;
import java.util.Optional;

import debt_payments.domain.model.PortfolioWriteOff;
public interface IPortfolioWriteOffQueryUseCase {
    /**
     * Busca un registro de castigo por su ID.
     * @param writeOffId El ID del castigo a buscar.
     * @return Un Optional del modelo de dominio.
     */
    Optional<PortfolioWriteOff> findById(Long writeOffId);

    /**
     * Lista todos los registros de castigo para una empresa específica.
     * @param enterpriseId El ID de la empresa.
     * @return Una lista de modelos de dominio.
     */
    List<PortfolioWriteOff> findByEnterpriseId(String enterpriseId);
}
