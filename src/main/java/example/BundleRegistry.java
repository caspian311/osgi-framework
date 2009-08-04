package example;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class BundleRegistry implements IBundleRegistry {
	private final BundleContext bundleContext;
	private final Map<String, Bundle> registry = new HashMap<String, Bundle>();

	public BundleRegistry(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void installBundle(File file) {
		try {
			Bundle bundle = bundleContext.installBundle(file.getAbsolutePath());
			registry.put(file.getAbsolutePath(), bundle);
		} catch (BundleException e) {
			throw new BundleRegistryException(e);
		}
	}

	public void uninstallBundle(File file) throws BundleRegistryException {
		Bundle bundle = registry.get(file.getAbsolutePath());
		if (bundle == null) {
			throw new BundleRegistryException("Could not uninstall bundle: "
					+ file.getAbsolutePath());
		}

		try {
			bundle.uninstall();
		} catch (BundleException e) {
			throw new BundleRegistryException(e);
		}
	}
}
