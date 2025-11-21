package debt_payments.application.output;

public interface IAccountEventPublisher {
    void publishAccountCreatedEvent(Long account, String enterpriseId);
}
