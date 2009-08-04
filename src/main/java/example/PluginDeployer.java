package example;

import java.io.File;

public class PluginDeployer {
	private final File pluginDirectory;
	private final IBundleRegistry bundleRegistry;

	public PluginDeployer(File pluginDirectory, IBundleRegistry bundleRegistry) {
		this.pluginDirectory = pluginDirectory;
		this.bundleRegistry = bundleRegistry;
	}

	public void deployPlugins() {
		String[] allFilesInDirectory = pluginDirectory.list();
		for (String filename : allFilesInDirectory) {
			if (filename.endsWith(".jar")) {
				File bundleToInstall = new File(pluginDirectory, filename);
				bundleRegistry.installBundle(bundleToInstall);
			}
		}
	}
}
