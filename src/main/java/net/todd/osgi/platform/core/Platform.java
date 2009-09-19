package net.todd.osgi.platform.core;

import java.io.File;

import net.todd.osgi.platform.IBundleRegistry;
import net.todd.osgi.platform.IPluginDeployer;

public class Platform {
	private final IBundleRegistry bundleRegistry;

	public Platform(IBundleRegistry bundleRegistry) {
		this.bundleRegistry = bundleRegistry;
	}

	public void staticDeployments(File pluginDirectory) {
		deploy(pluginDirectory, new PluginDeployer());
	}

	public void dynamicDeployments(File pluginDirectory) {
		deploy(pluginDirectory, new HotDeployer(1000));
	}

	public void deploy(File pluginDirectory, IPluginDeployer deployer) {
		deployer.deployPlugins(pluginDirectory, bundleRegistry);
	}
}
