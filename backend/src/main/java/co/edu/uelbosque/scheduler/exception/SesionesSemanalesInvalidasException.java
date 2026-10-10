package co.edu.uelbosque.scheduler.exception;

public class SesionesSemanalesInvalidasException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SesionesSemanalesInvalidasException(String mensaje) {
		super(mensaje);
	}
}
