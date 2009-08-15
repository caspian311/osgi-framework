package net.todd.osgi.platform;

import static org.junit.Assert.assertEquals;

import net.todd.osgi.platform.ThreadDaemon;

import org.junit.Test;

public class ThreadDaemonTest {
	private int runCount;

	@Test
	public void threadDaemonShouldRunTheRunnableThenSleepForGivenSleepTime()
			throws InterruptedException {
		ThreadDaemon threadDaemon = new ThreadDaemon("test daemon", new Runnable() {
			public void run() {
				runCount++;
			}
		}, 100);

		threadDaemon.startup();
		Thread.sleep(500);
		threadDaemon.shutdown();

		assertEquals(5, runCount, 1);
	}
}
