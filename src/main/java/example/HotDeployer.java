package example;

import java.io.File;

public class HotDeployer implements IPluginDeployer {
	public void deployPlugins(File pluginDirectory, IBundleRegistry bundleRegistry) {
		DirectoryWatcher directoryWatcher = new DirectoryWatcher(pluginDirectory);
		new ServiceManager(bundleRegistry, directoryWatcher);
	}
}
