package example;

public class ThreadDaemon {
	private final long sleeptime;
	private Thread thread;

	private final Runnable runnable;

	public ThreadDaemon(Runnable runnable, long sleeptime) {
		this.runnable = runnable;
		this.sleeptime = sleeptime;
	}

	public void startup() {
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

	public void shutdown() {
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
