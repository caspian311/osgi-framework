package example;

import java.io.File;
import java.util.HashMap;

import org.apache.felix.framework.Felix;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class Main {
	public static void main(String[] args) {
		String pluginDirectoryLocation = null;
		if (args.length > 0) {
			pluginDirectoryLocation = args[0];
		}

		final Felix felix = new Felix(new HashMap<String, Object>());
		try {
			felix.start();
		} catch (BundleException e) {
			throw new RuntimeException(e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					felix.stop();
				} catch (BundleException e) {
					throw new RuntimeException(e);
				}
			}
		});

		BundleContext bundleContext = felix.getBundleContext();
		IBundleRegistry bundleRegistry = new BundleRegistry(bundleContext);

		File pluginDirectory = new File(pluginDirectoryLocation);
		new PluginDeployer(pluginDirectory, bundleRegistry).deployPlugins();
		new HotDeployer(pluginDirectory, bundleRegistry).launch();
	}
}
