package debt_payments.infraestructure.output.jpa.repository.replicas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import debt_payments.infraestructure.output.jpa.entity.replicas.InvoiceReplicaEntity;

@Repository
public interface IInvoiceRepository extends JpaRepository<InvoiceReplicaEntity, Long> {
    /**
     * Encuentra todas las facturas de un cliente específico (ThirdParty)
     * cuyo saldo pendiente sea mayor a cero.
     *
     * @param thirdId El ID del cliente.
     * @param pendingValue El valor contra el que se compara el saldo pendiente (normalmente cero).
     * @return Una lista de entidades de facturas con saldo pendiente.
     */
    List<InvoiceReplicaEntity> findByThirdIdAndPendingValueGreaterThan(Long thirdId, Long pendingValue);
}
