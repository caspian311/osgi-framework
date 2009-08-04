package example;

import java.util.HashMap;

import org.apache.felix.framework.Felix;
import org.osgi.framework.BundleException;
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
		return new Felix(new HashMap<String, Object>());
	}
}
