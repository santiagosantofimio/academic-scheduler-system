package co.edu.uelbosque.scheduler.util;

import org.hibernate.proxy.HibernateProxy;

public final class IdentidadJpa {
	private IdentidadJpa() {
	}

	public static Class<?> tipo(Object object) {
		return object instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass()
				: object.getClass();
	}
}
