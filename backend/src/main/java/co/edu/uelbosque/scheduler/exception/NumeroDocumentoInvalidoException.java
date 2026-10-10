package co.edu.uelbosque.scheduler.exception;

public class NumeroDocumentoInvalidoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NumeroDocumentoInvalidoException(String mensaje) {
		super(mensaje);
	}
}
