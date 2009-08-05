package example;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PluginDeployer implements IPluginDeployer {
	private static final Log LOG = LogFactory.getLog(PluginDeployer.class);

	public void deployPlugins(File pluginDirectory, IBundleRegistry bundleRegistry) {
		String[] allFilesInDirectory = pluginDirectory.list();
		if (allFilesInDirectory != null) {
			for (String filename : allFilesInDirectory) {
				if (filename.endsWith(".jar")) {
					File bundleToInstall = new File(pluginDirectory, filename);
					LOG.info("Deploying plugin: " + bundleToInstall.getAbsolutePath());
					bundleRegistry.installBundle(bundleToInstall);
				}
			}
		}
	}
}
