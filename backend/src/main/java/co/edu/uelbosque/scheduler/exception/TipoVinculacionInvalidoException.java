package co.edu.uelbosque.scheduler.exception;

public class TipoVinculacionInvalidoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TipoVinculacionInvalidoException(String mensaje) {
		super(mensaje);
	}
}
