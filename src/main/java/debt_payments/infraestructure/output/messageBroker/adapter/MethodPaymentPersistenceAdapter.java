package debt_payments.infraestructure.output.messageBroker.adapter;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import debt_payments.infraestructure.output.jpa.entity.replicas.MethodPaymentReplicaEntity;
import debt_payments.infraestructure.output.jpa.repository.replicas.IMethodPaymentRepository;
import debt_payments.infraestructure.output.messageBroker.dto.MethodPaymentSyncDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MethodPaymentPersistenceAdapter {
    private final IMethodPaymentRepository methodPaymentRepository;

    public void saveOrUpdate(MethodPaymentSyncDto dto) {
        MethodPaymentReplicaEntity entity = methodPaymentRepository.findById(dto.getMethodPaymentId())
            .orElse(new MethodPaymentReplicaEntity());
        
        entity.setMethodPaymentId(dto.getMethodPaymentId());
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setActive(dto.isActive());
        entity.setEnterpriseId(dto.getEnterpriseId());
        entity.setLastUpdateAt(LocalDate.now());

        methodPaymentRepository.save(entity);
    }

    public void delete(Long methodPaymentId) {
        methodPaymentRepository.deleteById(methodPaymentId);
    }
}
