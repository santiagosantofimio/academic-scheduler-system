package co.edu.uelbosque.scheduler.exception;

public class PublicacionRechazadaException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PublicacionRechazadaException(String mensaje) {
		super(mensaje);
	}
}
