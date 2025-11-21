package debt_payments.domain.model.used;

import debt_payments.domain.ports.ResourceUsageNotification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ThirdPartyUsedNotification implements ResourceUsageNotification {
    private final Long thirdId;
    private final String enterpriseId;
}
