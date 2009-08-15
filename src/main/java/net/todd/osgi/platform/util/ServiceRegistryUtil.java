package net.todd.osgi.platform.util;

import java.util.ArrayList;
import java.util.List;

import net.todd.osgi.platform.IServiceEventHandler;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public class ServiceRegistryUtil {
	@SuppressWarnings("unchecked")
	public static <T> List<T> getServiceImplementations(final BundleContext context,
			Class<T> serviceClass) {
		ServiceReference[] allServiceReferences;
		try {
			Filter widgetFilter = objectClassFilter(context, serviceClass);
			allServiceReferences = context.getAllServiceReferences(null, widgetFilter.toString());
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}

		List<T> implemenatations = new ArrayList<T>();

		if (allServiceReferences != null) {
			for (ServiceReference serviceReference : allServiceReferences) {
				implemenatations.add((T) context.getService(serviceReference));
			}
		}

		return implemenatations;
	}

	public static void addServiceListener(final BundleContext context,
			Class<?> serviceClass, final IServiceEventHandler handler) {
		try {
			Filter widgetFilter = objectClassFilter(context, serviceClass);
			context.addServiceListener(new ServiceListener() {
				public void serviceChanged(ServiceEvent serviceEvent) {
					handler.handleServiceEvent(context, serviceEvent.getServiceReference(),
							serviceEvent.getType());
				}
			}, widgetFilter.toString());
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static Filter objectClassFilter(final BundleContext context, Class<?> serviceClass)
			throws InvalidSyntaxException {
		return context.createFilter("(" + Constants.OBJECTCLASS + "=" + serviceClass.getName()
				+ ")");
	}
}
