package net.todd.osgi.platform.core;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import net.todd.osgi.platform.core.OsgiFramework;

import org.junit.Test;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

public class OsgiFrameworkTest {
	@Test
	public void shouldAlwaysGetAFramework() {
		assertNotNull(new OsgiFramework().initializeFramework());
	}

	@Test
	public void throwExceptionWhenCantStart() throws Exception {
		String errorMessage = UUID.randomUUID().toString();

		final Framework mockFramework = createMock(Framework.class);
		mockFramework.init();
		mockFramework.start();
		expectLastCall().andThrow(new BundleException(errorMessage));
		replay(mockFramework);

		OsgiFramework osgiFramework = new OsgiFramework() {
			@Override
			Framework implemenatation() {
				return mockFramework;
			}
		};
		try {
			osgiFramework.initializeFramework();
			fail("Should have failed");
		} catch (Exception e) {
			assertTrue(e.getMessage().contains(errorMessage));
		}

		verify(mockFramework);
	}
}
