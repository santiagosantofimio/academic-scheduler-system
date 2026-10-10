package co.edu.uelbosque.scheduler.exception;

public class DatosInvalidosException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DatosInvalidosException(String mensaje) {
		super(mensaje);
	}
}
