package debt_payments;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:testdb",
	"spring.datasource.driverClassName=org.h2.Driver",
	"spring.jpa.hibernate.ddl-auto=update"
})
class DebtPaymentsApplicationTests {

	@Test
	void contextLoads() {
	}

}
