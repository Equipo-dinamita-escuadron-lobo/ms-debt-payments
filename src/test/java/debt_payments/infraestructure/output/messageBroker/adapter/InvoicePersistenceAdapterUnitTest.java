package debt_payments.infraestructure.output.messageBroker.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.infraestructure.output.jpa.entity.replicas.InvoiceReplicaEntity;
import debt_payments.infraestructure.output.jpa.mapper.replicas.IInvoicePersistenceMapper;
import debt_payments.infraestructure.output.jpa.repository.replicas.IInvoiceRepository;
import debt_payments.infraestructure.output.messageBroker.dto.InvoiceSyncDto;
import debt_payments.test.fixtures.TestFixtures;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvoicePersistenceAdapter unit tests")
public class InvoicePersistenceAdapterUnitTest {

    @Mock
    IInvoicePersistenceMapper mapper;

    @Mock
    IInvoiceRepository repository;

    // instantiate manually using constructor
    private InvoicePersistenceAdapter adapter() {
        return new InvoicePersistenceAdapter(mapper, repository);
    }

    @Test
    void saveOrUpdate_savesNewEntityWhenMissing() {
        InvoiceSyncDto dto = new InvoiceSyncDto(100L, "ENT-1", 11L, 2000L, 100L, 1900L, LocalDate.now().minusDays(5), LocalDate.now().plusDays(10), true, 500L);

        when(repository.findById(100L)).thenReturn(Optional.empty());

        InvoicePersistenceAdapter a = adapter();
        a.saveOrUpdate(dto);

        ArgumentCaptor<InvoiceReplicaEntity> cap = ArgumentCaptor.forClass(InvoiceReplicaEntity.class);
        verify(repository).save(cap.capture());

        InvoiceReplicaEntity saved = cap.getValue();
        assertThat(saved.getFactCode()).isEqualTo(100L);
        assertThat(saved.getEntId()).isEqualTo("ENT-1");
        assertThat(saved.getThirdId()).isEqualTo(11L);
        assertThat(saved.getTotalValue()).isEqualTo(2000L);
        assertThat(saved.getPendingValue()).isEqualTo(1900L);
        assertThat(saved.getAccountingAccount()).isEqualTo(500L);
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    void delete_delegatesToRepository() {
        InvoicePersistenceAdapter a = adapter();
        a.delete(55L);
        verify(repository).deleteById(55L);
    }

    @Test
    void getInvoiceBalance_returnsPendingValue() {
        InvoiceReplicaEntity e = new InvoiceReplicaEntity();
        e.setId(7L);
        e.setPendingValue(777L);

        when(repository.findById(7L)).thenReturn(Optional.of(e));

        InvoicePersistenceAdapter a = adapter();
        Optional<Long> bal = a.getInvoiceBalance(7L);

        assertThat(bal).isPresent().contains(777L);
    }

    @Test
    void findInvoiceById_usesMapperToDomain() {
        InvoiceReplicaEntity e = new InvoiceReplicaEntity();
        e.setId(9L);
        when(repository.findById(9L)).thenReturn(Optional.of(e));

        InvoiceReplica domain = TestFixtures.invoiceReplicaWith(9L, 1000L, 500L, null);
        when(mapper.toDomain(e)).thenReturn(domain);

        InvoicePersistenceAdapter a = adapter();
        Optional<InvoiceReplica> res = a.findInvoiceById(9L);

        assertThat(res).isPresent().contains(domain);
        verify(mapper).toDomain(e);
    }

    @Test
    void updateInvoice_updatesFieldsAndSaves() {
        InvoiceReplicaEntity existing = new InvoiceReplicaEntity();
        existing.setId(12L);
        existing.setPendingValue(1000L);
        existing.setTotalPay(0L);
        existing.setTotalValue(1000L);

        when(repository.getReferenceById(12L)).thenReturn(existing);

        InvoiceReplica domain = TestFixtures.invoiceReplicaWith(12L, 2000L, 1500L, null);

        InvoicePersistenceAdapter a = adapter();
        a.updateInvoice(domain);

        verify(repository).save(existing);
        assertThat(existing.getPendingValue()).isEqualTo(domain.getPendingValue());
        assertThat(existing.getTotalPay()).isEqualTo(domain.getTotalPay());
        assertThat(existing.getTotalValue()).isEqualTo(domain.getTotalValue());
    }

    @Test
    void findPendingInvoicesByClientId_mapsListThroughMapper() {
        InvoiceReplicaEntity e = new InvoiceReplicaEntity();
        e.setId(21L);
        when(repository.findByThirdIdAndPendingValueGreaterThan(5L, 0L)).thenReturn(List.of(e));

        InvoiceReplica domain = TestFixtures.invoiceReplicaWith(21L, 1000L, 100L, null);
        when(mapper.toInvoiceReplicaList(List.of(e))).thenReturn(List.of(domain));

        InvoicePersistenceAdapter a = adapter();
        var list = a.findPendingInvoicesByClientId(5L);

        assertThat(list).hasSize(1).contains(domain);
    }
}

