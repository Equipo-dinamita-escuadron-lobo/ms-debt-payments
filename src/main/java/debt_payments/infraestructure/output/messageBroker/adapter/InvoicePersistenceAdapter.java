package debt_payments.infraestructure.output.messageBroker.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.infraestructure.output.jpa.entity.replicas.InvoiceReplicaEntity;
import debt_payments.infraestructure.output.jpa.mapper.replicas.IInvoicePersistenceMapper;
import debt_payments.infraestructure.output.jpa.repository.replicas.IInvoiceRepository;
import debt_payments.infraestructure.output.messageBroker.dto.InvoiceSyncDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvoicePersistenceAdapter implements IInvoiceProviderPort {

    private final IInvoicePersistenceMapper invoiceMapper;
    private final IInvoiceRepository invoiceRepository;

    @Transactional
    public void saveOrUpdate(InvoiceSyncDto dto) {
        InvoiceReplicaEntity entity = invoiceRepository.findById(dto.getFactCode())
            .orElse(new InvoiceReplicaEntity());
        
        entity.setFactCode(dto.getFactCode());
        entity.setEntId(dto.getEntId());
        entity.setThirdId(dto.getThirdId());
        entity.setTotalValue(dto.getTotalValue());
        entity.setTotalPay(dto.getTotalPay());
        entity.setPendingValue(dto.getPendingValue());
        entity.setExpirationDate(dto.getExpirationDate());
        entity.setLastUpdateAt(LocalDate.now());
        entity.setActive(true); 
        entity.setAccountingAccount(dto.getAccountingAccount());
        invoiceRepository.save(entity);
    }

    public void delete(Long factCode) {
        invoiceRepository.deleteById(factCode);
    }

    @Override
    public Optional<Long> getInvoiceBalance(Long invoiceId) {
        Optional<InvoiceReplicaEntity> invoiceEntityOptional = invoiceRepository.findById(invoiceId);

        return invoiceEntityOptional.map(InvoiceReplicaEntity::getPendingValue);
    }

    /**
     * Busca una factura por su ID en la base de datos.
     * @param invoiceId El ID de la factura a buscar.
     * @return Un Optional que contiene el objeto de dominio InvoiceReplica si se encuentra,
     *         o un Optional vacío si no.
     */
    //@Transactional(readOnly = true) // Es una operación de solo lectura
    @Override
    public Optional<InvoiceReplica> findInvoiceById(Long invoiceId) {
        Optional<InvoiceReplicaEntity> entityOptional = invoiceRepository.findById(invoiceId);
        return entityOptional.map(invoiceMapper::toDomain);
    }

    /**
     * Actualiza una factura en la base de datos.
     * @param invoice El objeto de dominio InvoiceReplica con los datos actualizados.
     */
    @Override
    public void updateInvoice(InvoiceReplica invoice) {
        InvoiceReplicaEntity invoiceToUpdate = invoiceRepository.getReferenceById(invoice.getId());
        invoiceToUpdate.setPendingValue(invoice.getPendingValue());
        invoiceToUpdate.setTotalPay(invoice.getTotalPay());
        invoiceToUpdate.setTotalValue(invoice.getTotalValue());
        invoiceRepository.save(invoiceToUpdate);
    }

    @Override
    public List<InvoiceReplica> findPendingInvoicesByClientId(Long clientId) {
        var invoiceEntityList = invoiceRepository.findByThirdIdAndPendingValueGreaterThan(clientId, 0L);
        return invoiceMapper.toInvoiceReplicaList(invoiceEntityList);
    }

}
