package debt_payments;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba unitaria simple para validación de CI/CD
 */
public class SimpleUnitTest {

    @Test
    public void testBasicAssertion() {
        assertTrue(true, "Esta prueba siempre debe pasar");
    }

    @Test
    public void testSimpleAddition() {
        int resultado = 2 + 2;
        assertTrue(resultado == 4, "La suma de 2 + 2 debe ser 4");
    }
}
