package example;

public class FileSystemWatcherLauncher {
	private final long sleeptime;
	private Thread thread;

	private final Runnable runnable;

	public FileSystemWatcherLauncher(Runnable runnable, long sleeptime) {
		this.runnable = runnable;
		this.sleeptime = sleeptime;
	}

	public void startup() throws Exception {
		thread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					runnable.run();

					try {
						Thread.sleep(sleeptime);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}, "File System Watcher");
		thread.setDaemon(true);
		thread.start();
	}
}
