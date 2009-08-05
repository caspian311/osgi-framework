package example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
		FileInputStream inputstream = null;
		try {
			String bundleLocation;
			try {
				bundleLocation = file.toURI().toURL().toExternalForm();
				inputstream = new FileInputStream(file);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			Bundle bundle = bundleContext.installBundle(bundleLocation, inputstream);
			registry.put(file.getAbsolutePath(), bundle);
			bundle.start();
		} catch (BundleException e) {
			throw new BundleRegistryException(e);
		} finally {
			try {
				inputstream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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
