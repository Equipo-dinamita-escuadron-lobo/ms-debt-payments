package debt_payments.application.output;

import java.util.List;

import debt_payments.domain.ports.ResourceUsageNotification;

public interface IResourceUsageNotifierPort {
    /**
     * Notify all provided resource usage notifications.
     * @param notifications A list of ResourceUsageNotification instances to be notified.
     */
    void notifyAll(List<ResourceUsageNotification> notifications);
}
