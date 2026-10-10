package co.edu.uelbosque.scheduler.exception;

import java.net.URI;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import co.edu.uelbosque.scheduler.exception.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(RecursoNoEncontradoException.class)
	public ProblemDetail noEncontrado(RecursoNoEncontradoException error, HttpServletRequest request) {
		return problema(HttpStatus.NOT_FOUND, error.getMessage(), request);
	}

	@ExceptionHandler({ DuplicadoException.class, CruceHorarioException.class, DisponibilidadInvalidaException.class,
			AsignacionAcademicaInvalidaException.class })
	public ProblemDetail conflicto(RuntimeException error, HttpServletRequest request) {
		return problema(HttpStatus.CONFLICT, error.getMessage(), request);
	}

	@ExceptionHandler({ IdInvalidoException.class, NombreInvalidoException.class,
			NumeroDocumentoInvalidoException.class, CorreoInstitucionalInvalidoException.class,
			TipoVinculacionInvalidoException.class, NivelFormacionInvalidoException.class,
			AreaConocimientoInvalidaException.class, SemestreInvalidoException.class, CreditosInvalidosException.class,
			SesionesSemanalesInvalidasException.class, GrupoValidoException.class, HorarioInvalidoException.class,
			EstadoInvalidoException.class, DatosInvalidosException.class })
	public ProblemDetail datosInvalidos(RuntimeException error, HttpServletRequest request) {
		return problema(HttpStatus.BAD_REQUEST, error.getMessage(), request);
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
			MissingServletRequestParameterException.class, MethodArgumentNotValidException.class })
	public ProblemDetail formato(Exception error, HttpServletRequest request) {
		return problema(HttpStatus.BAD_REQUEST,
				"Solicitud invalida: revise el cuerpo, los parametros, las fechas y los valores de enumeraciones",
				request);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ProblemDetail integridad(DataIntegrityViolationException error, HttpServletRequest request) {
		return problema(HttpStatus.CONFLICT,
				"La operacion viola una restriccion de unicidad o existen recursos relacionados", request);
	}

	@ExceptionHandler(ConcurrencyFailureException.class)
	public ProblemDetail concurrencia(ConcurrencyFailureException error, HttpServletRequest request) {
		return problema(HttpStatus.CONFLICT,
				"El recurso fue modificado concurrentemente; consulte su estado e intente nuevamente", request);
	}

	private ProblemDetail problema(HttpStatus status, String detalle, HttpServletRequest request) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalle);
		problema.setTitle(status.getReasonPhrase());
		problema.setInstance(URI.create(request.getRequestURI()));
		return problema;
	}
}
