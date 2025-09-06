package debt_payments.infraestructure.output.messageBroker.adapter;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import debt_payments.application.output.IThirdPartyProviderPort;
import debt_payments.infraestructure.output.jpa.entity.replicas.ThirdPartyReplicaEntity;
import debt_payments.infraestructure.output.jpa.repository.replicas.IThirdRepository;
import debt_payments.infraestructure.output.messageBroker.dto.ThirdSyncDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ThirdPartyPersistenceAdapter implements IThirdPartyProviderPort {

    private final IThirdRepository thirdRepository;

    @Override
    public boolean thirdPartyExists(Long thirdPartyId) {
        return thirdRepository.existsById(thirdPartyId);
    }
    
    //Metodo que listener usara para guardar o actualizar 
    public void saveOrUpdate(ThirdSyncDto dto) {
        ThirdPartyReplicaEntity entity = thirdRepository.findById(dto.getThirdPartyId())
            .orElse(new ThirdPartyReplicaEntity());
        
        entity.setId(dto.getThirdPartyId());
        entity.setName(dto.getName());
        entity.setIdentificationNumber(dto.getIdentificationNumber());
        entity.setEnterpriseId(dto.getEnterpriseId());
        entity.setActive(dto.isActive());
        entity.setLastUpdateAt(LocalDate.now());
        
        thirdRepository.save(entity);
    }

    public void delete(Long thirdPartyId) {
        thirdRepository.deleteById(thirdPartyId);
    }
}
