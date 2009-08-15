package net.todd.osgi.platform.core;

public class ThreadDaemon {
	private final Thread thread;

	public ThreadDaemon(String threadName, final Runnable runnable, final long sleeptime) {
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
		}, threadName);
		thread.setDaemon(true);
	}

	public void startup() {
		thread.start();
	}

	public void shutdown() {
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
