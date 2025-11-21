package debt_payments.domain.model.used;

import debt_payments.domain.ports.ResourceUsageNotification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class AccountUsedNotification implements ResourceUsageNotification {
    private final Long account; // Internamente usamos un nombre consistente
    private final String enterpriseId;
    private final String sourceAccountType; // "ID" o "CODE"
}
