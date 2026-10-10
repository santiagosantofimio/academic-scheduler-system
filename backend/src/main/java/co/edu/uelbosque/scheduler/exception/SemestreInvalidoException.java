package co.edu.uelbosque.scheduler.exception;

public class SemestreInvalidoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SemestreInvalidoException(String mensaje) {
		super(mensaje);
	}
}
