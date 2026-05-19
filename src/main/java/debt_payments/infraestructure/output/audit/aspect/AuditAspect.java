package debt_payments.infraestructure.output.audit.aspect;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.domain.model.Receipt;
import debt_payments.infraestructure.output.audit.annotation.DocumentAuditable;
import debt_payments.infraestructure.output.audit.annotation.DocumentOperationType;
import debt_payments.infraestructure.output.audit.builder.AuditEventBuilder;
import debt_payments.infraestructure.output.audit.builder.DocumentDataBuilder;
import debt_payments.infraestructure.output.audit.builder.DocumentEventDto;
import debt_payments.infraestructure.output.audit.publisher.AuditEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditEventBuilder auditEventBuilder;
    private final DocumentDataBuilder documentDataBuilder;
    private final AuditEventPublisher auditEventPublisher;

    @Around("@annotation(documentAuditable)")
    public Object audit(ProceedingJoinPoint joinPoint, DocumentAuditable documentAuditable) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object result = joinPoint.proceed();

        try {
            DocumentContext ctx = extractContext(documentAuditable.operationType(), args, result);
            if (ctx == null) {
                log.warn("No se pudo extraer contexto de cartera para operación {}", documentAuditable.operationType());
                return result;
            }

            Map<String, Object> documentData = documentDataBuilder.build(
                    documentAuditable.operationType(), args, result);

            DocumentEventDto event = auditEventBuilder.build(
                    documentAuditable.moduleName(),
                    documentAuditable.operationType(),
                    ctx.enterpriseId(),
                    ctx.documentId(),
                    ctx.documentCode(),
                    ctx.documentType(),
                    ctx.documentDate(),
                    ctx.thirdPartyId(),
                    ctx.thirdPartyName(),
                    documentData);

            auditEventPublisher.publish(event);

        } catch (Exception e) {
            log.error("Error en auditoría de cartera [operation={}]: {}",
                    documentAuditable.operationType(), e.getMessage(), e);
        }

        return result;
    }

    private DocumentContext extractContext(
            DocumentOperationType operationType, Object[] args, Object result) {
        return switch (operationType) {
            case CREATE -> extractFromCreateResult(result);
            case APPROVE -> extractFromWriteOffResult(result, "WRITE_OFF");
            case VOID -> extractFromVoidResult(result);
            default -> null;
        };
    }

    private DocumentContext extractFromCreateResult(Object result) {
        if (result instanceof Receipt receipt) {
            return new DocumentContext(
                    receipt.getId() != null ? receipt.getId().toString() : null,
                    receipt.getReceiptCode(),
                    receipt.getEnterpriseId(),
                    receipt.getReceiptType() != null ? "RECEIPT_" + receipt.getReceiptType().name() : "RECEIPT",
                    receipt.getIssueDate(),
                    receipt.getThirdPartyId() != null ? receipt.getThirdPartyId().toString() : null,
                    receipt.getThirdPartyId() != null ? "Cliente " + receipt.getThirdPartyId() : null);
        }
        if (result instanceof PortfolioWriteOff writeOff) {
            return new DocumentContext(
                    writeOff.getId() != null ? writeOff.getId().toString() : null,
                    writeOff.getCode(),
                    writeOff.getEnterpriseId(),
                    "WRITE_OFF",
                    writeOff.getWriteOffDate(),
                    writeOff.getThirdId() != null ? writeOff.getThirdId().toString() : null,
                    writeOff.getThirdId() != null ? "Cliente " + writeOff.getThirdId() : null);
        }
        return null;
    }

    private DocumentContext extractFromWriteOffResult(Object result, String documentType) {
        if (result instanceof PortfolioWriteOff writeOff) {
            return new DocumentContext(
                    writeOff.getId() != null ? writeOff.getId().toString() : null,
                    writeOff.getCode(),
                    writeOff.getEnterpriseId(),
                    documentType,
                    writeOff.getWriteOffDate(),
                    writeOff.getThirdId() != null ? writeOff.getThirdId().toString() : null,
                    writeOff.getThirdId() != null ? "Cliente " + writeOff.getThirdId() : null);
        }
        return null;
    }

    private DocumentContext extractFromVoidResult(Object result) {
        // Tanto Receipt como PortfolioWriteOff son posibles resultados de VOID
        if (result instanceof Receipt receipt) {
            return new DocumentContext(
                    receipt.getId() != null ? receipt.getId().toString() : null,
                    receipt.getReceiptCode(),
                    receipt.getEnterpriseId(),
                    receipt.getReceiptType() != null ? "RECEIPT_" + receipt.getReceiptType().name() : "RECEIPT",
                    receipt.getVoidDate() != null ? receipt.getVoidDate() : receipt.getIssueDate(),
                    receipt.getThirdPartyId() != null ? receipt.getThirdPartyId().toString() : null,
                    receipt.getThirdPartyId() != null ? "Cliente " + receipt.getThirdPartyId() : null);
        }
        if (result instanceof PortfolioWriteOff writeOff) {
            return extractFromWriteOffResult(writeOff, "WRITE_OFF");
        }
        return null;
    }

}
