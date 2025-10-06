package debt_payments.domain.enums;

public enum WriteOffStatus {
    PENDING_CONFIRMATION, // Guardado, pero sin afectar saldos ni contabilidad.
    CONFIRMED,            // Afecta saldos y notifica a contabilidad.
    VOIDED                // Anulado, revierte saldos y notifica a contabilidad.
}
