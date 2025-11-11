package debt_payments.infraestructure.output.exception.handler;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpStatus;

import debt_payments.domain.exception.InvoiceNotFoundException;
import debt_payments.domain.exception.PortfolioWriteOffNotFoundException;
import debt_payments.domain.exception.ReceiptNotFoundException;
import debt_payments.infraestructure.output.exception.dto.ErrorResponseDto;

@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handler to Resource Not Found (404)
     * @param ex The exception that was thrown
     * @param request The web request during which the exception was thrown
     * @return A ResponseEntity containing the error details and a 404 status code
     */
    @ExceptionHandler({InvoiceNotFoundException.class, ReceiptNotFoundException.class, PortfolioWriteOffNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(RuntimeException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(), // El mensaje que pusimos en la excepción, ej: "Invoice 123 not found"
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handler to Illegal State (409)
     * @param ex The exception that was thrown
     * @param request The web request during which the exception was thrown
     * @return A ResponseEntity containing the error details and a 409 status code
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(), // Ej: "Only a CONFIRMED write-off can be voided."
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handler to Invalid Arguments (400)
     * @param ex The exception that was thrown
     * @param request The web request during which the exception was thrown
     * @return A ResponseEntity containing the error details and a 400 status code
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(), // Ej: "A justification is required for a write-off."
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handler to Generic Exceptions (500) - The Safety Net
     * This catches any other exceptions not explicitly handled.
     * @param ex The exception that was thrown
     * @param request The web request during which the exception was thrown
     * @return A ResponseEntity containing the error details and a 500 status code
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please contact support.", 
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
