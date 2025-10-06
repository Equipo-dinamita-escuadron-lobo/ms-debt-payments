package debt_payments.infraestructure.output.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import debt_payments.infraestructure.output.jpa.entity.PortfolioWriteOffEntity;

public interface IPortfolioWriteOffRepository extends JpaRepository<PortfolioWriteOffEntity, Long> {
    /**
     * Busca todos los registros de castigo asociados a un ID de empresa.
     * @param enterpriseId El ID de la empresa.
     * @return Una lista de entidades de castigo.
     */
    List<PortfolioWriteOffEntity> findByEnterpriseId(String enterpriseId);
}
