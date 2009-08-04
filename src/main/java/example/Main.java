package example;

import java.io.File;

import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;

public class Main {
	public static void main(String[] args) {
		if (args == null || args.length != 1) {
			throw new IllegalArgumentException("Need a plugin directory");
		}

		new Main().deployments(new File(args[0]));
	}

	private void deployments(File pluginDirectory) {
		IBundleRegistry bundleRegistry = createBundleRegistry();
		new PluginDeployer().deployPlugins(pluginDirectory, bundleRegistry);
		new HotDeployer().deployPlugins(pluginDirectory, bundleRegistry);
	}

	private IBundleRegistry createBundleRegistry() {
		Framework framework = new OsgiFramework().initializeFramework();
		BundleContext bundleContext = framework.getBundleContext();
		return new BundleRegistry(bundleContext);
	}
}
