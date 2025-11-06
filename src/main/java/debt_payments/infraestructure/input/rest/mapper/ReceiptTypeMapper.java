package debt_payments.infraestructure.input.rest.mapper;

import org.springframework.stereotype.Component;

import debt_payments.domain.enums.ReceiptType;

@Component
public class ReceiptTypeMapper {
    public ReceiptType toReceiptType(Long id) {
        if (id == null) return null;
        for (ReceiptType type : ReceiptType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ReceiptType ID: " + id);
    }

    public Long toLong(ReceiptType type) {
        return (type == null) ? null : type.getId();
    }
}
