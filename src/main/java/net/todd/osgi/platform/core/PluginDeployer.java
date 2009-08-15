package net.todd.osgi.platform.core;

import java.io.File;

import net.todd.osgi.platform.IBundleRegistry;
import net.todd.osgi.platform.IPluginDeployer;

public class PluginDeployer implements IPluginDeployer {
	public void deployPlugins(File pluginDirectory, IBundleRegistry bundleRegistry) {
		String[] allFilesInDirectory = pluginDirectory.list();
		if (allFilesInDirectory != null) {
			for (String filename : allFilesInDirectory) {
				if (filename.endsWith(".jar")) {
					File bundleToInstall = new File(pluginDirectory, filename);
					bundleRegistry.installBundle(bundleToInstall);
				}
			}
		}
	}
}
