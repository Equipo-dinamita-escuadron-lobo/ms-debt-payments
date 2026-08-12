package debt_payments.application.service;

import java.util.List;

import debt_payments.application.input.IPortfolioWriteOffCommandUseCase;
import debt_payments.application.input.IPortfolioWriteOffQueryUseCase;
import debt_payments.application.output.IAccountingEventPublisher;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.application.output.IPortfolioWriteOffPersistencePort;
import debt_payments.application.output.IResourceUsageNotifierPort;
import debt_payments.domain.exception.PortfolioWriteOffNotFoundException;
import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.domain.model.Replica.InvoiceReplica;
import lombok.RequiredArgsConstructor;

/**
 * @brief Service class for managing portfolio write-offs.
 *        This class implements both command and query use cases for portfolio
 *        write-offs,
 *        handling creation, confirmation, voidance, and retrieval operations.
 *        It orchestrates interactions between the domain model and various
 *        persistence
 *        and notification ports.
 */

@RequiredArgsConstructor
public class PortfolioWriteOffService implements IPortfolioWriteOffCommandUseCase, IPortfolioWriteOffQueryUseCase {

    private final IPortfolioWriteOffPersistencePort writeOffPersistencePort;
    private final IInvoiceProviderPort invoiceProviderPort;
    private final IAccountingEventPublisher accountingEventPublisher;
    private final IResourceUsageNotifierPort resourceUsageNotifier;

    @Override
    public PortfolioWriteOff createWriteOff(PortfolioWriteOff writeOff) {
        // 1. El objeto de dominio ya viene creado y validado desde la capa de
        // REST/Mapper.
        // Le pedimos al dominio que prepare las facturas.
        List<InvoiceReplica> modifiedInvoices = writeOff
                .prepareInvoicesForWriteOff(invoiceProviderPort::findInvoiceById);

        writeOff.setCode(generateUniqueWriteOffCode());

        // 3. Orquestar la persistencia
        for (InvoiceReplica invoice : modifiedInvoices) {
            invoiceProviderPort.updateInvoice(invoice);
        }

        // 4. Publicar tercero y centro de costo usados
        resourceUsageNotifier.notifyAll(writeOff.getUsageNotifications());

        return writeOffPersistencePort.save(writeOff);
    }

    @Override
    public PortfolioWriteOff confirmWriteOff(Long writeOffId) {
        // 1. Cargar el Agregado Raíz
        PortfolioWriteOff writeOff = findWriteOffOrThrow(writeOffId);

        // 2. Ejecutar la lógica de negocio en el dominio
        writeOff.confirm(); // Cambia el estado interno
        List<InvoiceReplica> modifiedInvoices = writeOff.processConfirmation(invoiceProviderPort::findInvoiceById);

        // 3. Orquestar la persistencia
        for (InvoiceReplica invoice : modifiedInvoices) {
            invoiceProviderPort.updateInvoice(invoice);
        }
        PortfolioWriteOff updatedWriteOff = writeOffPersistencePort.update(writeOff);

        // 4. Publicar evento de dominio
        accountingEventPublisher.publishWriteOffConfirmedEvent(updatedWriteOff);

        return updatedWriteOff;
    }

    @Override
    public PortfolioWriteOff voidWriteOffConfirmation(Long writeOffId) {
        // 1. Cargar el Agregado Raíz
        PortfolioWriteOff writeOff = findWriteOffOrThrow(writeOffId);

        // 2. Ejecutar la lógica de negocio en el dominio
        writeOff.voidConfirmation();
        List<InvoiceReplica> modifiedInvoices = writeOff.processVoidance(invoiceProviderPort::findInvoiceById);

        // 3. Orquestar la persistencia
        for (InvoiceReplica invoice : modifiedInvoices) {
            invoiceProviderPort.updateInvoice(invoice);
        }
        PortfolioWriteOff updatedWriteOff = writeOffPersistencePort.update(writeOff);

        // 4. Publicar evento de dominio
        accountingEventPublisher.publishWriteOffVoidedEvent(updatedWriteOff);

        return updatedWriteOff;
    }

    @Override
    public PortfolioWriteOff findById(Long writeOffId) {
        return findWriteOffOrThrow(writeOffId);
    }

    @Override
    public List<PortfolioWriteOff> findByEnterpriseId(String enterpriseId) {
        return writeOffPersistencePort.findByEnterpriseId(enterpriseId);
    }

    /**
     * Busca un castigo por su ID o lanza una excepción si no se encuentra.
     * 
     * @param writeOffId ID del castigo a buscar
     * @return El castigo encontrado
     * @throws RuntimeException si no se encuentra el castigo
     */
    private PortfolioWriteOff findWriteOffOrThrow(Long writeOffId) {
        return writeOffPersistencePort.findById(writeOffId)
                .orElseThrow(() -> new PortfolioWriteOffNotFoundException(
                        "Castigo de cartera con ID: " + writeOffId + " no fue encontrado."));
    }

    private String generateUniqueWriteOffCode() {
        return "CC-" + System.currentTimeMillis();
    }

    @Override
    public List<PortfolioWriteOff> findConfirmedOrVoidedByEnterpriseId(String enterpriseId) {
        return writeOffPersistencePort.findConfirmedOrVoidedByEnterpriseId(enterpriseId);
    }
}
