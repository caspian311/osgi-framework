package net.todd.osgi.platform;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public interface IServiceEventHandler {
	void handleServiceEvent(BundleContext context, ServiceReference serviceReference, int type);
}
