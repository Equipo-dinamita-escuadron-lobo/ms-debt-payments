package debt_payments.infraestructure.output.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import debt_payments.infraestructure.output.jpa.entity.ReceiptEntity;

@Repository
public interface IReceiptRepository extends JpaRepository<ReceiptEntity, Long> {

    //ReceiptEntity save(ReceiptEntity receiptEntity);

    Optional<ReceiptEntity> findById(Long id);

    List<ReceiptEntity> findByThirdPartyId(Long thirdPartyId);

    List<ReceiptEntity> findAllByEnterpriseId(String enterpriseId);

    boolean existsById(Long id);

    /**
     * Finds all receipts that contain a detail associated with a specific invoice ID.
     * Encuentra todos los recibos que contienen un detalle asociado a un ID de factura específico.
     * @param invoiceId The ID of the invoice.
     * @return A list of matching ReceiptEntity objects.
     */
    @Query("SELECT r FROM ReceiptEntity r JOIN r.details d WHERE d.invoiceId = :invoiceId")
    List<ReceiptEntity> findByInvoiceId(@Param("invoiceId") Long invoiceId);
}
