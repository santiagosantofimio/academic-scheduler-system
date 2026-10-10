package co.edu.uelbosque.scheduler.exception;

public class EstadoInvalidoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EstadoInvalidoException(String mensaje) {
		super(mensaje);
	}
}
