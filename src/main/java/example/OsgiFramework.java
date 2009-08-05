package example;

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
		String cacheLocation;
		try {
			final File cacheDirectory = File.createTempFile(getClass().getName() + "-cache", null);
			cacheLocation = cacheDirectory.getAbsolutePath();
			FileUtils.forceDelete(cacheDirectory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Map<String, Object> configMap = new HashMap<String, Object>();
		configMap.put(Constants.FRAMEWORK_STORAGE, cacheLocation);
		return new Felix(configMap);
	}
}
