package debt_payments.application.input;

import java.util.List;
import java.util.Optional;

import debt_payments.infraestructure.input.rest.dto.response.PortfolioWriteOffResponse;

public interface IPortfolioWriteOffQueryUseCase {
    /**
     * Busca un registro de castigo por su ID y lo enriquece con los datos de las facturas.
     * @param writeOffId El ID del castigo a buscar.
     * @return Un DTO de respuesta con toda la información necesaria para el frontend.
     */
    Optional<PortfolioWriteOffResponse> findById(Long writeOffId);

    /**
     * Lista todos los registros de castigo para una empresa específica.
     * @param enterpriseId El ID de la empresa.
     * @return Una lista de DTOs de respuesta con la información de los castigos.
     */
    List<PortfolioWriteOffResponse> findByEnterpriseId(String enterpriseId);
}
