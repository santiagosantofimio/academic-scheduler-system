package co.edu.uelbosque.scheduler.exception;

public class DuplicadoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DuplicadoException(String mensaje) {
		super(mensaje);
	}
}
