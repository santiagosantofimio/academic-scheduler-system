package co.edu.uelbosque.scheduler.exception;

public class CorreoInstitucionalInvalidoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CorreoInstitucionalInvalidoException(String mensaje) {
		super(mensaje);
	}
}
