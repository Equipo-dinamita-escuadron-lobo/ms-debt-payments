package debt_payments.infraestructure.output.jpa.repository.replicas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import debt_payments.infraestructure.output.jpa.entity.replicas.AccountReplicaEntity;

@Repository
public interface IAccountRepository extends JpaRepository<AccountReplicaEntity, Long> {

}
