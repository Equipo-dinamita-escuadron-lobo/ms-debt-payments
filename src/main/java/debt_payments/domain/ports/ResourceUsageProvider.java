package debt_payments.domain.ports;

import java.util.List;

public interface ResourceUsageProvider {
    /**
     * Retorna una lista de "intenciones de notificación" por los recursos que esta entidad utiliza.
     * @return Una lista de objetos que implementan ResourceUsageNotification.
     */
    List<ResourceUsageNotification> getUsageNotifications();
}
