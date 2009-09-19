package net.todd.osgi.platform.core;

import java.io.File;

import net.todd.osgi.platform.IBundleRegistry;

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

		IBundleRegistry bundleRegistry = createBundleRegistry();
		Platform platform = new Platform(bundleRegistry);
		platform.staticDeployments(pluginDirectory);
		platform.dynamicDeployments(pluginDirectory);
	}

	private static IBundleRegistry createBundleRegistry() {
		Framework framework = new OsgiFramework().initializeFramework();
		BundleContext bundleContext = framework.getBundleContext();
		return new BundleRegistry(bundleContext);
	}
}
