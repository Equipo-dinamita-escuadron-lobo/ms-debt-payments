package debt_payments.application.input;

import debt_payments.domain.model.PortfolioWriteOff;

/**
 * Puerto de Entrada para los comandos relacionados con el Castigo de Cartera.
 */
public interface IPortfolioWriteOffCommandUseCase {

    /**
     * Crea un nuevo registro de castigo.
     * @param writeOff El objeto de dominio a crear (ya validado).
     * @return El modelo de dominio del castigo creado.
     */
    PortfolioWriteOff createWriteOff(PortfolioWriteOff writeOff);

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
