package net.todd.osgi.platform.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.todd.osgi.platform.BundleRegistryException;
import net.todd.osgi.platform.IBundleRegistry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class BundleRegistry implements IBundleRegistry {
	private static final Log LOG = LogFactory.getLog(BundleRegistry.class);
	private final BundleContext bundleContext;
	private final Map<String, Bundle> registry = new HashMap<String, Bundle>();

	public BundleRegistry(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void installBundle(File file) {
		LOG.info("Installing bundle: " + file.getAbsolutePath());
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
		LOG.info("Uninstalling bundle: " + file.getAbsolutePath());
		Bundle bundle = registry.get(file.getAbsolutePath());
		if (bundle == null) {
			throw new BundleRegistryException("Could not uninstall bundle: "
					+ file.getAbsolutePath());
		}

		try {
			bundle.stop();
			bundle.uninstall();
		} catch (BundleException e) {
			throw new BundleRegistryException(e);
		}
	}
}
