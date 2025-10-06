package debt_payments.application.input;

import debt_payments.domain.model.PortfolioWriteOff;
import debt_payments.infraestructure.input.rest.dto.request.CreateWriteOffRequest;

/**
 * Puerto de Entrada para los comandos relacionados con el Castigo de Cartera.
 */
public interface IPortfolioWriteOffCommandUseCase {

    /**
     * Crea un nuevo registro de castigo en estado PENDING_CONFIRMATION.
     * @param request DTO con la información para crear el castigo.
     * @return El modelo de dominio del castigo creado.
     */
    PortfolioWriteOff createWriteOff(CreateWriteOffRequest request);

    /**
     * Confirma un castigo de cartera existente.
     * @param writeOffId El ID del castigo a confirmar.
     * @return El modelo de dominio del castigo actualizado.
     */
    PortfolioWriteOff confirmWriteOff(Long writeOffId);

    /**
     * Anula la confirmación de un castigo de cartera.
     * @param writeOffId El ID del castigo a anular.
     * @return El modelo de dominio del castigo actualizado.
     */
    PortfolioWriteOff voidWriteOffConfirmation(Long writeOffId);
}
