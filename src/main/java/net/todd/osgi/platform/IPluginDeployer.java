package net.todd.osgi.platform;

import java.io.File;

public interface IPluginDeployer {
	void deployPlugins(File pluginDirectory, IBundleRegistry bundleRegistry);
}
