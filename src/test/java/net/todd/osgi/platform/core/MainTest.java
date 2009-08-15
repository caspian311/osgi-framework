package net.todd.osgi.platform.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.UUID;

import net.todd.osgi.platform.core.Main;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class MainTest {
	@Test
	public void ifNullIsPassedInForArgsAnIllegalStateExceptionIsThrown() {
		try {
			Main.main(null);
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
			assertEquals("Need a plugin directory", e.getMessage());
		}
	}

	@Test
	public void ifNoArgsAreGivenAnIllegalStateExceptionIsThrown() {
		try {
			Main.main(new String[] {});
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
			assertEquals("Need a plugin directory", e.getMessage());
		}
	}

	@Test
	public void exceptionIsThrownIfArgumentIsNotADirectory() {
		String pluginDirectoryLocation = UUID.randomUUID().toString();
		try {
			Main.main(new String[] { pluginDirectoryLocation });
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
			assertEquals("Need a plugin directory", e.getMessage());
		}
	}

	@Test
	public void noExceptionIsThrownIfGoodArgumentIsPassedIn() throws Exception {
		File tempFile = File.createTempFile(getClass().getName(), null);
		String pluginDirectoryLocation = tempFile.getAbsolutePath();
		FileUtils.forceDelete(tempFile);

		File pluginDirectory = new File(pluginDirectoryLocation);

		try {
			pluginDirectory.mkdir();
			Main.main(new String[] { pluginDirectoryLocation });
		} finally {
			FileUtils.deleteDirectory(pluginDirectory);
		}
	}
}
