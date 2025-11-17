package debt_payments.infraestructure.output.messageBroker.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.infraestructure.output.messageBroker.dto.PortfolioWriteOffEventDto;
import debt_payments.test.fixtures.TestFixtures;

@DisplayName("IPortfolioWriteOffEventMapper (MapStruct) tests")
public class IPortfolioWriteOffEventMapperTest {

    private final IPortfolioWriteOffEventMapper mapper = new IPortfolioWriteOffEventMapperImpl();

    @Test
    @DisplayName("toEventDto should map PortfolioWriteOff -> PortfolioWriteOffEventDto ignoring details")
    void toEventDto_mapsDomainToEventDto() {
        var detail = TestFixtures.writeOffDetail(3L, 120L);
        PortfolioWriteOff writeOff = TestFixtures.portfolioWriteOffWithDetails("ENT-1", 5L, "just", List.of(detail));
        writeOff.setId(77L);

        PortfolioWriteOffEventDto dto = mapper.toEventDto(writeOff);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(77L);
        assertThat(dto.getEnterpriseId()).isEqualTo("ENT-1");
        // details are ignored by mapping
        assertThat(dto.getDetails()).isNull();
    }
}
