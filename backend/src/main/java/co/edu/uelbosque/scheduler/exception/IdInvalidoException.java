package co.edu.uelbosque.scheduler.exception;

public class IdInvalidoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IdInvalidoException(String mensaje) {
		super(mensaje);
	}
}
