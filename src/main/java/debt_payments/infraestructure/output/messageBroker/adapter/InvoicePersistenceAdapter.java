package debt_payments.infraestructure.output.messageBroker.adapter;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;

import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.infraestructure.output.jpa.entity.replicas.InvoiceReplicaEntity;
import debt_payments.infraestructure.output.jpa.repository.replicas.IInvoiceRepository;
import debt_payments.infraestructure.output.messageBroker.dto.InvoiceSyncDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvoicePersistenceAdapter implements IInvoiceProviderPort {

    private final IInvoiceRepository invoiceRepository;

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

}
