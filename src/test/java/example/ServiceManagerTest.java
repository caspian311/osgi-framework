package example;

import static org.easymock.EasyMock.createMock;

import org.junit.Test;
import org.osgi.framework.BundleContext;

public class ServiceManagerTest {
	@Test
	public void test() {
		BundleContext bundleContext = createMock(BundleContext.class);
		IFileSystemWatcher fileSystemWatcher = createMock(IFileSystemWatcher.class);
		new ServiceManager(bundleContext, fileSystemWatcher);
	}
}
