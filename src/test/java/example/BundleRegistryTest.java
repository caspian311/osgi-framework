package example;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class BundleRegistryTest {
	@Test
	public void installingABundleWithTheRegistryInstallsThePluginInTheUnderlyingBundleContext()
			throws Exception {
		File file = File.createTempFile(getClass().getName(), ".jar");

		try {
			BundleContext bundleContext = createMock(BundleContext.class);
			Bundle bundle = createMock(Bundle.class);
			expect(bundleContext.installBundle(file.getAbsolutePath())).andReturn(bundle);
			replay(bundleContext);

			BundleRegistry registry = new BundleRegistry(bundleContext);
			registry.installBundle(file);

			verify(bundleContext);
		} finally {
			FileUtils.forceDelete(file);
		}
	}

	@Test
	public void throwBundleExceptionIfExceptionIsThrownWhileInstallingThePlugin() throws Exception {
		File file = File.createTempFile(getClass().getName(), ".jar");

		try {
			BundleContext bundleContext = createMock(BundleContext.class);
			String errorMessage = "Could not install bundle: " + file.getAbsolutePath();
			expect(bundleContext.installBundle(file.getAbsolutePath())).andThrow(
					new BundleException(errorMessage));
			replay(bundleContext);

			BundleRegistry registry = new BundleRegistry(bundleContext);
			try {
				registry.installBundle(file);
				fail("Should have thrown an exception");
			} catch (BundleRegistryException e) {
				assertTrue(e.getMessage().contains(errorMessage));
			}

			verify(bundleContext);
		} finally {
			FileUtils.forceDelete(file);
		}
	}

	@Test
	public void throwBundleExceptionIfTryingToUninstallAPluginThatHasntAlreadyBeenInstalled()
			throws Exception {
		File file = File.createTempFile(getClass().getName(), ".jar");

		try {
			BundleContext bundleContext = createMock(BundleContext.class);
			replay(bundleContext);

			BundleRegistry registry = new BundleRegistry(bundleContext);
			try {
				registry.uninstallBundle(file);
				fail("Should have thrown an exception");
			} catch (BundleRegistryException e) {
				assertEquals("Could not uninstall bundle: " + file.getAbsolutePath(), e
						.getMessage());
			}

			verify(bundleContext);
		} finally {
			FileUtils.forceDelete(file);
		}
	}

	@Test
	public void bundleRegistryCanUninstallPreviouslyInstalledBundle() throws Exception {
		File file = File.createTempFile(getClass().getName(), ".jar");

		try {
			BundleContext bundleContext = createMock(BundleContext.class);
			Bundle bundle = createMock(Bundle.class);
			expect(bundleContext.installBundle(file.getAbsolutePath())).andReturn(bundle);
			bundle.uninstall();
			replay(bundleContext, bundle);

			BundleRegistry registry = new BundleRegistry(bundleContext);
			registry.installBundle(file);
			registry.uninstallBundle(file);

			verify(bundleContext, bundle);
		} finally {
			FileUtils.forceDelete(file);
		}
	}

	@Test
	public void ifBundleUninstallThrowsExceptionWrapItAndThrowItAgain() throws Exception {
		File file = File.createTempFile(getClass().getName(), ".jar");

		try {
			BundleContext bundleContext = createMock(BundleContext.class);
			Bundle bundle = createMock(Bundle.class);
			expect(bundleContext.installBundle(file.getAbsolutePath())).andReturn(bundle);
			bundle.uninstall();
			String errorMessage = UUID.randomUUID().toString();
			expectLastCall().andThrow(new BundleException(errorMessage));
			replay(bundleContext, bundle);

			BundleRegistry registry = new BundleRegistry(bundleContext);
			registry.installBundle(file);
			try {
				registry.uninstallBundle(file);
			} catch (BundleRegistryException e) {
				assertTrue(e.getMessage().contains(errorMessage));
			}

			verify(bundleContext, bundle);
		} finally {
			FileUtils.forceDelete(file);
		}
	}
}
