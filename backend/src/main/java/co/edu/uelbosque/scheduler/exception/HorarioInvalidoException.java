package co.edu.uelbosque.scheduler.exception;

public class HorarioInvalidoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public HorarioInvalidoException(String mensaje) {
		super(mensaje);
	}
}
