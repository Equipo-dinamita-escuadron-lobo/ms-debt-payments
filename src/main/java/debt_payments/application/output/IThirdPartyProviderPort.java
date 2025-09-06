package debt_payments.application.output;

public interface IThirdPartyProviderPort {
    boolean thirdPartyExists(Long thirdPartyId);
}
