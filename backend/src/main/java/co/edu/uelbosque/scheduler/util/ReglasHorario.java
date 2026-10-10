package co.edu.uelbosque.scheduler.util;

import java.time.*;
import co.edu.uelbosque.scheduler.entity.ConfiguracionHorarios;
import static co.edu.uelbosque.scheduler.util.LanzadorDeExcepcion.*;

/**
 * Intervalos [inicio, fin): dos sesiones que solo comparten un extremo no se
 * cruzan.
 */
public final class ReglasHorario {
	private ReglasHorario() {
	}

	public static boolean seCruzan(LocalTime a, LocalTime b, LocalTime c, LocalTime d) {
		return a.isBefore(d) && c.isBefore(b);
	}

	public static LocalTime inicio(ConfiguracionHorarios c, DayOfWeek dia) {
		return dia == DayOfWeek.SATURDAY ? c.getHoraInicioSabado() : c.getHoraInicioSemana();
	}

	public static LocalTime fin(ConfiguracionHorarios c, DayOfWeek dia) {
		return dia == DayOfWeek.SATURDAY ? c.getHoraFinSabado() : c.getHoraFinSemana();
	}

	public static boolean franja(ConfiguracionHorarios c, DayOfWeek dia, LocalTime desde, LocalTime hasta) {
		verificarHorario(desde, hasta);
		if (dia == null || dia == DayOfWeek.SUNDAY)
			return false;
		if (desde.isBefore(inicio(c, dia)) || hasta.isAfter(fin(c, dia)))
			return false;
		return c.getHoraInicioDescanso() == null
				|| !seCruzan(desde, hasta, c.getHoraInicioDescanso(), c.getHoraFinDescanso());
	}

	public static boolean consecutivos(DayOfWeek a, DayOfWeek b) {
		return Math.abs(a.getValue() - b.getValue()) == 1;
	}
}
