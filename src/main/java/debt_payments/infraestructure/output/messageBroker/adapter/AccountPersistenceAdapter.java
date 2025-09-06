package debt_payments.infraestructure.output.messageBroker.adapter;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import debt_payments.infraestructure.output.jpa.entity.replicas.AccountReplicaEntity;
import debt_payments.infraestructure.output.jpa.repository.replicas.IAccountRepository;
import debt_payments.infraestructure.output.messageBroker.dto.AccountSyncDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter {
    private final IAccountRepository accountRepository;

    public void saveOrUpdate(AccountSyncDto dto) {
        AccountReplicaEntity entity = accountRepository.findById(dto.getAccountId())
                .orElse(new AccountReplicaEntity());

        entity.setAccountId(dto.getAccountId());
        entity.setAccountNumber(dto.getAccountNumber());
        entity.setAccountName(dto.getAccountName());
        entity.setAccountType(dto.getAccountType());
        entity.setActive(dto.isActive());
        entity.setEnterpriseId(dto.getEnterpriseId());
        entity.setLastUpdateAt(LocalDate.now());

        accountRepository.save(entity);
    }

    public void delete(Long accountId) {
        accountRepository.deleteById(accountId);
    }
}
