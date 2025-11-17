package debt_payments.application.output;

public interface IThirdEventPublisher {
    void publishThirdUsedEvent(Long thirdId, String enterpriseId);
}
