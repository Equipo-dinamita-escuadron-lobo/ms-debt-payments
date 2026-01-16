package debt_payments.infraestructure.output.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpStatus;

import debt_payments.domain.exception.InvoiceNotFoundException;
import debt_payments.domain.exception.PortfolioWriteOffNotFoundException;
import debt_payments.domain.exception.ReceiptNotFoundException;
import debt_payments.infraestructure.input.rest.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the application's REST controllers.
 * It catches exceptions thrown from the controller layer and converts them
 * into a standardized {@link ApiResponse} format.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        // Método helper para obtener la URI
        private String getRequestPath(WebRequest request) {
                return ((ServletWebRequest) request).getRequest().getRequestURI();
        }

        // --- MANEJADORES PARA EXCEPCIONES "NOT FOUND" (404) ---

        @ExceptionHandler(InvoiceNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleInvoiceNotFoundException(InvoiceNotFoundException ex,
                        WebRequest request) {
                log.warn("InvoiceNotFoundException: {} at path {}", ex.getMessage(), getRequestPath(request));
                HttpStatus status = HttpStatus.NOT_FOUND;
                // <<< LLAMADA AL NUEVO MÉTODO ERROR
                ApiResponse<Object> apiResponse = ApiResponse.error(ex.getMessage(), "INVOICE_NOT_FOUND", status,
                                getRequestPath(request));
                return new ResponseEntity<>(apiResponse, status);
        }

        @ExceptionHandler(ReceiptNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleReceiptNotFoundException(ReceiptNotFoundException ex,
                        WebRequest request) {
                log.warn("ReceiptNotFoundException: {} at path {}", ex.getMessage(), getRequestPath(request));
                HttpStatus status = HttpStatus.NOT_FOUND;
                ApiResponse<Object> apiResponse = ApiResponse.error(ex.getMessage(), "RECEIPT_NOT_FOUND", status,
                                getRequestPath(request));
                return new ResponseEntity<>(apiResponse, status);
        }

        @ExceptionHandler(PortfolioWriteOffNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handlePortfolioWriteOffNotFoundException(
                        PortfolioWriteOffNotFoundException ex, WebRequest request) {
                log.warn("PortfolioWriteOffNotFoundException: {} at path {}", ex.getMessage(), getRequestPath(request));
                HttpStatus status = HttpStatus.NOT_FOUND;
                ApiResponse<Object> apiResponse = ApiResponse.error(ex.getMessage(), "WRITEOFF_NOT_FOUND", status,
                                getRequestPath(request));
                return new ResponseEntity<>(apiResponse, status);
        }

        // --- MANEJADORES PARA ERRORES DE LÓGICA DE NEGOCIO ---

        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(IllegalStateException ex,
                        WebRequest request) {
                log.warn("IllegalStateException: {} at path {}", ex.getMessage(), getRequestPath(request));
                HttpStatus status = HttpStatus.CONFLICT;
                ApiResponse<Object> apiResponse = ApiResponse.error(ex.getMessage(), "INVALID_STATE_OPERATION", status,
                                getRequestPath(request));
                return new ResponseEntity<>(apiResponse, status);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex,
                        WebRequest request) {
                log.warn("IllegalArgumentException: {} at path {}", ex.getMessage(), getRequestPath(request));
                HttpStatus status = HttpStatus.BAD_REQUEST;
                ApiResponse<Object> apiResponse = ApiResponse.error(ex.getMessage(), "INVALID_ARGUMENT", status,
                                getRequestPath(request));
                return new ResponseEntity<>(apiResponse, status);
        }

        // --- MANEJADOR GENÉRICO DE SEGURIDAD (500) ---

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, WebRequest request) {
                log.error("An unexpected error occurred at path {}: ", getRequestPath(request), ex);
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                ApiResponse<Object> apiResponse = ApiResponse.error(
                                "Ocurrió un error inesperado en el servidor. Por favor, contacte a soporte.",
                                "INTERNAL_SERVER_ERROR",
                                status,
                                getRequestPath(request));
                return new ResponseEntity<>(apiResponse, status);
        }
}