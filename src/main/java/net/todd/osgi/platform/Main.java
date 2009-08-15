package net.todd.osgi.platform;

import java.io.File;

import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;

public class Main {
	private static final String BAD_ARGUMENT_ERROR = "Need a plugin directory";

	public static void main(String[] args) {
		if (args == null || args.length != 1) {
			throw new IllegalArgumentException(BAD_ARGUMENT_ERROR);
		}

		File pluginDirectory = new File(args[0]);

		if (!pluginDirectory.isDirectory()) {
			throw new IllegalArgumentException(BAD_ARGUMENT_ERROR);
		}

		new Main().deployments(pluginDirectory);
	}

	private void deployments(File pluginDirectory) {
		IBundleRegistry bundleRegistry = createBundleRegistry();
		new PluginDeployer().deployPlugins(pluginDirectory, bundleRegistry);
		new HotDeployer(1000).deployPlugins(pluginDirectory, bundleRegistry);
	}

	private IBundleRegistry createBundleRegistry() {
		Framework framework = new OsgiFramework().initializeFramework();
		BundleContext bundleContext = framework.getBundleContext();
		return new BundleRegistry(bundleContext);
	}
}
