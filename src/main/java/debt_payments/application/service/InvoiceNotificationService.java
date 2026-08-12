package debt_payments.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import debt_payments.application.input.IInvoiceNotificationUseCase;
import debt_payments.application.output.IInvoiceNotificationEventPublisher;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.domain.model.InvoiceDueReminder;
import debt_payments.domain.model.InvoiceDueReminderDetail;
import debt_payments.domain.model.Replica.InvoiceReplica;
import lombok.RequiredArgsConstructor;

/***
 * @brief Service class for handling invoice notifications.
 * This class processes due invoices and publishes consolidated
 * notification events for clients with pending invoices.
 */

@RequiredArgsConstructor
public class InvoiceNotificationService implements IInvoiceNotificationUseCase {

    private final IInvoiceProviderPort invoiceProviderPort;
    private final IInvoiceNotificationEventPublisher notificationPublisher;
    private static final int DAYS_BEFORE_DUE = 5; 

    /**
     * @brief Process and publish due invoice notifications.
     * This method retrieves invoices that are due in a specified number of days,
     * groups them by third party ID, constructs consolidated notification events, 
     * and publishes them.
     */
    @Override
    public void processAndPublishDueInvoices() {
        LocalDate targetDate = LocalDate.now().plusDays(DAYS_BEFORE_DUE);
        List<InvoiceReplica> dueInvoices = invoiceProviderPort.findInvoicesByExpirationDate(targetDate);

        // 2. AGRUPAR las facturas por el ID del cliente (thirdPartyId)
        Map<Long, List<InvoiceReplica>> invoicesByClient = dueInvoices.stream()
                .collect(Collectors.groupingBy(InvoiceReplica::getThirdId));

        // 3. ITERAR sobre el mapa de clientes agrupados
        for (Map.Entry<Long, List<InvoiceReplica>> entry : invoicesByClient.entrySet()) {
            Long thirdPartyId = entry.getKey();
            List<InvoiceReplica> clientInvoices = entry.getValue();

            // 4. Construir el evento CONSOLIDADO para este cliente
            InvoiceDueReminder event = buildConsolidatedEvent(thirdPartyId, clientInvoices);
            
            // 5. Publicar UN ÚNICO evento por cliente
            notificationPublisher.publishInvoiceDueReminder(event);
        }
    }

    private InvoiceDueReminder buildConsolidatedEvent(Long thirdPartyId, List<InvoiceReplica> invoices) {
        // Mapear la lista de InvoiceReplica a una lista de InvoiceDetailDto
        List<InvoiceDueReminderDetail> invoiceDetails = invoices.stream()
                .map(this::buildInvoiceDetail)
                .collect(Collectors.toList());

        // Crear el DTO consolidado
        return new InvoiceDueReminder(thirdPartyId, invoiceDetails);
    }

    /**
     * @brief Build InvoiceDetailEventDto from InvoiceReplica.
     * @param invoice The InvoiceReplica instance.
     * @return The constructed InvoiceDetailEventDto.
     */
    private InvoiceDueReminderDetail buildInvoiceDetail(InvoiceReplica invoice) {
        return new InvoiceDueReminderDetail(invoice.getId(), Long.parseLong(invoice.getFactCode()),
                invoice.getExpirationDate(), invoice.getTotalValue(), invoice.getPendingValue());
    }
    
}
