package example;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PluginDeployerTest {
	private File pluginDirectory;

	@Before
	public void setUp() throws IOException {
		File tempFile = File.createTempFile(getClass().getName(), null);
		String pluginDirectoryLocation = tempFile.getAbsolutePath();
		FileUtils.forceDelete(tempFile);
		pluginDirectory = new File(pluginDirectoryLocation);
		pluginDirectory.mkdir();
	}

	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(pluginDirectory);
	}

	@Test
	public void pluginDeployerOnlyDeploysJarFilesAndIgnoresAllOtherFilesInDirectory()
			throws Exception {
		File plugin1 = new File(pluginDirectory, "test1.jar");
		File plugin2 = new File(pluginDirectory, "test2.jar");
		File nonPlugin = new File(pluginDirectory, "test3.txt");

		FileUtils.touch(plugin1);
		FileUtils.touch(plugin2);
		FileUtils.touch(nonPlugin);

		IBundleRegistry bundleRegistry = createMock(IBundleRegistry.class);
		bundleRegistry.installBundle(plugin1);
		bundleRegistry.installBundle(plugin2);
		replay(bundleRegistry);

		new PluginDeployer().deployPlugins(pluginDirectory, bundleRegistry);

		verify(bundleRegistry);
	}
}
