package co.edu.uelbosque.scheduler.exception;

public class NombreInvalidoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NombreInvalidoException(String mensaje) {
		super(mensaje);
	}
}
