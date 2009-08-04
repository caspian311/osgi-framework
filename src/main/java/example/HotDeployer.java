package example;

import java.io.File;

public class HotDeployer implements IPluginDeployer {
	public void deployPlugins(File pluginDirectory, IBundleRegistry bundleRegistry) {
		final DirectoryWatcher directoryWatcher = new DirectoryWatcher(pluginDirectory);
		final ThreadDaemon threadDaemon = new ThreadDaemon("File System Watcher", new Runnable() {
			public void run() {
				try {
					directoryWatcher.checkForChanges();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 1000);
		threadDaemon.startup();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				threadDaemon.shutdown();
			}
		});
		new ServiceManager(bundleRegistry, directoryWatcher);
	}
}
