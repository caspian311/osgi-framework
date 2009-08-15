package net.todd.osgi.platform;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.felix.framework.Felix;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

public class OsgiFramework {
	public Framework initializeFramework() {
		final Framework osgiFramework = implemenatation();
		try {
			osgiFramework.init();
			osgiFramework.start();
		} catch (BundleException e) {
			throw new RuntimeException(e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					osgiFramework.stop();
				} catch (BundleException e) {
					throw new RuntimeException(e);
				}
			}
		});
		return osgiFramework;
	}

	Framework implemenatation() {
		Map<String, Object> configuration = new HashMap<String, Object>();
		configuration.put(Constants.FRAMEWORK_STORAGE, cacheLocation());
		configuration.put(Constants.FRAMEWORK_BOOTDELEGATION, extraSystemPackages());

		return new Felix(configuration);
	}

	private String extraSystemPackages() {
		return "javax.swing, net.todd.osgi.platform.util";
	}

	private String cacheLocation() {
		String cacheLocation;
		try {
			final File cacheDirectory = File.createTempFile(getClass().getName() + "-cache", null);
			cacheLocation = cacheDirectory.getAbsolutePath();
			FileUtils.forceDelete(cacheDirectory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return cacheLocation;
	}
}
