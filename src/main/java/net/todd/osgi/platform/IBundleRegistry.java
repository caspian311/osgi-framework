package net.todd.osgi.platform;

import java.io.File;

public interface IBundleRegistry {
	void uninstallBundle(File file) throws BundleRegistryException;

	void installBundle(File file);
}
