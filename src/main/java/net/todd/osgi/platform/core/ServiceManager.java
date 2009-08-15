package net.todd.osgi.platform.core;

import java.io.File;

import net.todd.common.uitools.IListener;
import net.todd.osgi.platform.IBundleRegistry;
import net.todd.osgi.platform.IDirectoryWatcher;

public class ServiceManager {
	private final IBundleRegistry bundleRegistry;
	private final IDirectoryWatcher directoryWatcher;

	public ServiceManager(IBundleRegistry bundleRegistry, IDirectoryWatcher directoryWatcher) {
		this.bundleRegistry = bundleRegistry;
		this.directoryWatcher = directoryWatcher;

		directoryWatcher.addFileDeletedListener(new IListener() {
			public void fireEvent() {
				handleFileDeleted();
			}
		});

		directoryWatcher.addFileAddedListener(new IListener() {
			public void fireEvent() {
				handleFileAdded();
			}
		});

		directoryWatcher.addFileChangedListener(new IListener() {
			public void fireEvent() {
				handleFileChanged();
			}
		});
	}

	private void handleFileDeleted() {
		for (File deletedFile : directoryWatcher.getDeletedFiles()) {
			bundleRegistry.uninstallBundle(deletedFile);
		}
	}

	private void handleFileAdded() {
		for (File addedFile : directoryWatcher.getAddedFiles()) {
			bundleRegistry.installBundle(addedFile);
		}
	}

	private void handleFileChanged() {
		for (File changedFile : directoryWatcher.getChangedFiles()) {
			bundleRegistry.uninstallBundle(changedFile);
			bundleRegistry.installBundle(changedFile);
		}
	}
}
