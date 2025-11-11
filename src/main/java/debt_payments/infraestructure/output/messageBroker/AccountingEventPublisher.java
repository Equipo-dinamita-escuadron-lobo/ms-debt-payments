package debt_payments.infraestructure.output.messageBroker;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import debt_payments.application.input.IAccountingEventPublisher;
import debt_payments.application.output.IInvoiceProviderPort;
import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.domain.model.Receipt;
import debt_payments.domain.model.WriteOffDetail;
import debt_payments.domain.model.Replica.InvoiceReplica;
import debt_payments.infraestructure.config.RabbitAccountingConfig;
import debt_payments.infraestructure.output.messageBroker.dto.EventDto;
import debt_payments.infraestructure.output.messageBroker.dto.InvoiceSummaryEventDto;
import debt_payments.infraestructure.output.messageBroker.dto.PortfolioWriteOffEventDto;
import debt_payments.infraestructure.output.messageBroker.dto.ReceiptEventDto;
import debt_payments.infraestructure.output.messageBroker.dto.WriteOffDetailEventDto;
import debt_payments.infraestructure.output.messageBroker.mapper.IPortfolioWriteOffEventMapper;
import debt_payments.infraestructure.output.messageBroker.mapper.IReceiptEventMapper;
import debt_payments.infraestructure.output.security.IJwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountingEventPublisher implements IAccountingEventPublisher {
    private final IReceiptEventMapper receiptEventMapper;
    private final RabbitTemplate rabbitTemplate;
    private final IPortfolioWriteOffEventMapper writeOffEventMapper;
    private final IInvoiceProviderPort invoiceProviderPort;
    private final IJwtUtils jwtUtils;

    @Override
    public void publishReceiptCreatedEvent(Receipt receipt) {
        ReceiptEventDto receiptEventDto = receiptEventMapper.toEventDto(receipt);
        EventDto<ReceiptEventDto> event = new EventDto<>("RECEIPT_CREATED", receiptEventDto);
        log.info("Publishing receipt created event: {}", receiptEventDto.getReceiptCode());

        rabbitTemplate.convertAndSend(RabbitAccountingConfig.RECEIPT_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of(
                    "x-jwt-token", jwtUtils.getToken()
            ));
            return message;
        });
    }

    @Override
    public void publishVoidReceiptEvent(Receipt receipt) {
        ReceiptEventDto receiptEventDto = receiptEventMapper.toEventDto(receipt);
        EventDto<ReceiptEventDto> event = new EventDto<>("RECEIPT_VOIDED", receiptEventDto);
        log.info("Publishing receipt voided event: {}", receiptEventDto.getReceiptCode());

        rabbitTemplate.convertAndSend(RabbitAccountingConfig.RECEIPT_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of(
                    "x-jwt-token", jwtUtils.getToken()
            ));
            return message;
        });
    }

    @Override
    public void publishWriteOffConfirmedEvent(PortfolioWriteOff writeOff) {
        // 1. Construir el DTO del evento enriquecido
        PortfolioWriteOffEventDto eventDto = buildEnrichedEventDto(writeOff);
        
        // 2. Envolver y publicar
        EventDto<PortfolioWriteOffEventDto> event = new EventDto<>("WRITEOFF_CONFIRMED", eventDto);
        log.info("Publishing write-off confirmed event: {}", eventDto.getCode());

        rabbitTemplate.convertAndSend(RabbitAccountingConfig.WRITEOFF_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of("x-jwt-token", jwtUtils.getToken()));
            return message;
        });
    }

    @Override
    public void publishWriteOffVoidedEvent(PortfolioWriteOff writeOff) {
        // 1. Construir el DTO del evento enriquecido (reutilizamos la misma lógica)
        PortfolioWriteOffEventDto eventDto = buildEnrichedEventDto(writeOff);
        
        // 2. Envolver y publicar
        EventDto<PortfolioWriteOffEventDto> event = new EventDto<>("WRITEOFF_VOIDED", eventDto);
        log.info("Publishing write-off voided event: {}", eventDto.getCode());
        
        rabbitTemplate.convertAndSend(RabbitAccountingConfig.WRITEOFF_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of("x-jwt-token", jwtUtils.getToken()));
            return message;
        });
    }

    private PortfolioWriteOffEventDto buildEnrichedEventDto(PortfolioWriteOff writeOff) {
        // a. Usar el mapper simple para la cabecera
        PortfolioWriteOffEventDto eventDto = writeOffEventMapper.toEventDto(writeOff);

        // b. Enriquecer los detalles (lógica idéntica a la del Controller)
        List<Long> invoiceIds = writeOff.getDetails().stream().map(WriteOffDetail::getInvoiceId).toList();
        
        Map<Long, InvoiceReplica> invoiceMap = invoiceProviderPort.findInvoicesByIds(invoiceIds).stream()
                .collect(Collectors.toMap(InvoiceReplica::getId, Function.identity()));
        
        List<WriteOffDetailEventDto> detailDtos = writeOff.getDetails().stream()
            .map(detail -> {
                InvoiceReplica invoice = invoiceMap.get(detail.getInvoiceId());
                if (invoice == null) return null;

                InvoiceSummaryEventDto invoiceSummary = InvoiceSummaryEventDto.builder()
                        .id(invoice.getId())
                        .factCode(invoice.getFactCode())
                        .totalValue(invoice.getTotalValue())
                        .pendingValue(detail.getAmountWrittenOff()) // Saldo antes del castigo
                        .expirationDate(invoice.getExpirationDate())
                        .accountingAccount(invoice.getAccountingAccount())
                        .build();
                
                return WriteOffDetailEventDto.builder()
                        .amountWrittenOff(detail.getAmountWrittenOff())
                        .invoice(invoiceSummary)
                        .build();
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        eventDto.setDetails(detailDtos);
        return eventDto;
    }
    
}
