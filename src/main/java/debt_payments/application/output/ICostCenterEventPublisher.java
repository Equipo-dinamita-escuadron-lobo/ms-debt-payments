package debt_payments.application.output;

public interface ICostCenterEventPublisher {
    void publishCostCenterUsedEvent(Long costCenterId, String enterpriseId);
}
