package debt_payments;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class Controller {
    @GetMapping("/prueba")
    public String getMethodName() {
        return "Si funciona";
    }
}
