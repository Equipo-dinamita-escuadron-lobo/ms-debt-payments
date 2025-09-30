package debt_payments.infraestructure.output.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import debt_payments.application.output.IReceiptCommandPersistencePort;
import debt_payments.application.output.IReceiptQueryPersistencePort;
import debt_payments.domain.model.Receipt;
import debt_payments.infraestructure.output.jpa.entity.ReceiptEntity;
import debt_payments.infraestructure.output.jpa.mapper.IReceiptPersistenceMapper;
import debt_payments.infraestructure.output.jpa.repository.IReceiptRepository;
import lombok.RequiredArgsConstructor;

/**
 * Persistence Adapter for Receipts.
 * This class implements the output ports for persistence and acts as a bridge
 * between the application layer and the JPA-based persistence technology.
 *
 * Adaptador de Persistencia para Recibos.
 * Esta clase implementa los puertos de salida para la persistencia y actúa como
 * un puente
 * entre la capa de aplicación y la tecnología de persistencia basada en JPA.
 */
@Repository
@RequiredArgsConstructor
public class ReceiptPersistenceAdapter implements IReceiptCommandPersistencePort, IReceiptQueryPersistencePort {

    private final IReceiptRepository receiptRepository;
    private final IReceiptPersistenceMapper receiptMapper;

    @Override
    public Receipt save(Receipt receipt) {
        ReceiptEntity receiptEntity = receiptMapper.toEntity(receipt);

        ReceiptEntity savedEntity = receiptRepository.save(receiptEntity);
        return receiptMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Receipt> findById(Long id) {
        Optional<ReceiptEntity> entityOpt = receiptRepository.findById(id);
        return entityOpt.map(receiptMapper::toDomain);
    }

    @Override
    public List<Receipt> findByInvoiceId(String invoiceId) {
        List<ReceiptEntity> entityList = receiptRepository.findByInvoiceId(Long.valueOf(invoiceId));
        return receiptMapper.toDomainList(entityList);
    }

    @Override
    public List<Receipt> findByThirdPartyId(String thirdPartyId) {
        List<ReceiptEntity> entityList = receiptRepository.findByThirdPartyId(Long.valueOf(thirdPartyId));
        return receiptMapper.toDomainList(entityList);
    }

    @Override
    public List<Receipt> findByEnterpriseId(String enterpriseId) {
        List<ReceiptEntity> entityList = receiptRepository.findAllByEnterpriseId(enterpriseId);
        return receiptMapper.toDomainList(entityList);
    }

    @Override
    public boolean existsById(Long id) {
        return receiptRepository.existsById(id);
    }

}
