package co.edu.uelbosque.scheduler.util;

import java.time.*;
import java.util.Comparator;
import java.util.List;
import co.edu.uelbosque.scheduler.entity.ConfiguracionHorarios;
import co.edu.uelbosque.scheduler.entity.DisponibilidadDocente;
import static co.edu.uelbosque.scheduler.util.LanzadorDeExcepcion.*;

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

	public static boolean dentroDeDisponibilidad(List<DisponibilidadDocente> ventanas, DayOfWeek dia, LocalTime desde,
			LocalTime hasta) {
		if (ventanas.isEmpty())
			return true;
		LocalTime cubierto = desde;
		for (DisponibilidadDocente v : delDia(ventanas, dia)) {
			if (v.getHoraInicio().isAfter(cubierto))
				return false;
			if (v.getHoraFin().isAfter(cubierto))
				cubierto = v.getHoraFin();
			if (!cubierto.isBefore(hasta))
				return true;
		}
		return false;
	}

	public static List<DisponibilidadDocente> delDia(List<DisponibilidadDocente> ventanas, DayOfWeek dia) {
		return ventanas.stream().filter(v -> v.getDiaSemana() == dia)
				.sorted(Comparator.comparing(DisponibilidadDocente::getHoraInicio)).toList();
	}

	public static int cupoPermitido(ConfiguracionHorarios c) {
		return c.getCupoMaximoGrupo() * (100 + c.getToleranciaCupoPorcentaje()) / 100;
	}

	public static boolean cupoValido(ConfiguracionHorarios c, int cupo) {
		return cupo >= c.getCupoMinimoGrupo() && cupo <= cupoPermitido(c);
	}

	public static boolean consecutivos(DayOfWeek a, DayOfWeek b) {
		return Math.abs(a.getValue() - b.getValue()) == 1;
	}
}
