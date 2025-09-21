package debt_payments.infraestructure.output.messageBroker.recovery;

import org.springframework.stereotype.Component;

import debt_payments.domain.ports.IEventRecoveryActionPort;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.InvoiceSyncDto;

@Component
public class InvoiceRecoveryAction implements IEventRecoveryActionPort<EventDto<InvoiceSyncDto>> {

    @Override
    public boolean executeRecoveryAction(EventDto<InvoiceSyncDto> event) {
        // TODO por hacer para Rodrigo
        throw new UnsupportedOperationException("Unimplemented method 'executeRecoveryAction'");
    }

    @Override
    public boolean canHandle(EventDto<InvoiceSyncDto> event) {
        // TODO por hacer para Rodrigo
        throw new UnsupportedOperationException("Unimplemented method 'canHandle'");
    }
    
}
