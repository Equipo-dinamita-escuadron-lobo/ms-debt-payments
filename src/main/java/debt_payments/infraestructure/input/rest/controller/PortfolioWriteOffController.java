package debt_payments.infraestructure.input.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import debt_payments.application.input.IPortfolioWriteOffCommandUseCase;
import debt_payments.application.input.IPortfolioWriteOffQueryUseCase;
import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.infraestructure.input.rest.dto.request.CreateWriteOffRequest;
import debt_payments.infraestructure.input.rest.dto.response.PortfolioWriteOffResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments/write-offs")
@RequiredArgsConstructor
public class PortfolioWriteOffController {
    
    private final IPortfolioWriteOffCommandUseCase commandUseCase;
    private final IPortfolioWriteOffQueryUseCase queryUseCase;

    @PostMapping("/")
    public ResponseEntity<PortfolioWriteOffResponse> createWriteOff(@Valid @RequestBody CreateWriteOffRequest request) {
        PortfolioWriteOff createdWriteOff = commandUseCase.createWriteOff(request);

        // El servicio de consulta es el que sabe enriquecer la respuesta.
        PortfolioWriteOffResponse response = queryUseCase.findById(createdWriteOff.getId())
            .orElseThrow(() -> new IllegalStateException("Newly created write-off could not be found."));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<PortfolioWriteOffResponse> confirmWriteOff(@PathVariable Long id) {
        commandUseCase.confirmWriteOff(id);
        
        PortfolioWriteOffResponse response = queryUseCase.findById(id)
            .orElseThrow(() -> new IllegalStateException("Confirmed write-off could not be found."));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/void")
    public ResponseEntity<PortfolioWriteOffResponse> voidWriteOffConfirmation(@PathVariable Long id) {
        commandUseCase.voidWriteOffConfirmation(id);
        
        PortfolioWriteOffResponse response = queryUseCase.findById(id)
            .orElseThrow(() -> new IllegalStateException("Voided write-off could not be found."));

        return ResponseEntity.ok(response);
    }

    // --- Endpoints de CONSULTAS (GET) ---

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioWriteOffResponse> getWriteOffById(@PathVariable Long id) {
        return queryUseCase.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-enterprise/{enterpriseId}")
    public ResponseEntity<List<PortfolioWriteOffResponse>> getWriteOffsByEnterprise(@PathVariable String enterpriseId) {
        List<PortfolioWriteOffResponse> responses = queryUseCase.findByEnterpriseId(enterpriseId);
        
        if (responses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(responses);
    }

}
