package co.edu.uelbosque.scheduler.exception;

public class CreditosInvalidosException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CreditosInvalidosException(String mensaje) {
		super(mensaje);
	}
}
