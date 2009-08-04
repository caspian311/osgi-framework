package example;

import java.io.File;

public class HotDeployer implements IPluginDeployer {
	private final long sleeptime;

	public HotDeployer(long sleeptime) {
		this.sleeptime = sleeptime;
	}

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
		}, sleeptime);
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
