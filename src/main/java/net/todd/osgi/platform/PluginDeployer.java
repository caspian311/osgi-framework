package net.todd.osgi.platform;

import java.io.File;

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
