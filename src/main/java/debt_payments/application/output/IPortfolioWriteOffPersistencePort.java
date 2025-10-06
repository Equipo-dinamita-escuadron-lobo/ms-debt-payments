package debt_payments.application.output;

import java.util.List;
import java.util.Optional;

import debt_payments.domain.model.PortfolioWriteOff;

public interface IPortfolioWriteOffPersistencePort {
    /**
     * Guarda un nuevo registro de castigo en la base de datos.
     * @param writeOff El modelo de dominio a persistir.
     * @return El modelo de dominio persistido (con el ID asignado).
     */
    PortfolioWriteOff save(PortfolioWriteOff writeOff);

    /**
     * Busca un registro de castigo por su ID.
     * @param writeOffId El ID a buscar.
     * @return Un Optional con el modelo de dominio si se encuentra.
     */
    Optional<PortfolioWriteOff> findById(Long writeOffId);
    
    /**
     * Actualiza un registro de castigo existente.
     * @param writeOff El modelo de dominio con los datos actualizados.
     * @return El modelo de dominio actualizado.
     */
    PortfolioWriteOff update(PortfolioWriteOff writeOff);

    /**
     * Lista todos los registros de castigo para una empresa específica.
     * @param enterpriseId El ID de la empresa.
     * @return Una lista de modelos de dominio con los castigos encontrados.
     */
    List<PortfolioWriteOff> findByEnterpriseId(String enterpriseId);
}
