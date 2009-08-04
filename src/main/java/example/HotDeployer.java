package example;

import java.io.File;

public class HotDeployer {
	private final File pluginDirectory;
	private final IBundleRegistry bundleRegistry;

	public HotDeployer(File pluginDirectory, IBundleRegistry bundleRegistry) {
		this.pluginDirectory = pluginDirectory;
		this.bundleRegistry = bundleRegistry;
	}

	public void launch() {
		DirectoryWatcher directoryWatcher = new DirectoryWatcher(pluginDirectory);
		new ServiceManager(bundleRegistry, directoryWatcher);
	}
}
