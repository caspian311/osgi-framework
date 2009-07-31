package example;

import java.io.File;

import net.todd.common.uitools.IListener;

public class ServiceManager {
	private final IBundleRegistry bundleRegistry;
	private final IFileSystemWatcher fileSystemWatcher;

	public ServiceManager(IBundleRegistry bundleRegistry, IFileSystemWatcher fileSystemWatcher) {
		this.bundleRegistry = bundleRegistry;
		this.fileSystemWatcher = fileSystemWatcher;

		fileSystemWatcher.addFileDeletedListener(new IListener() {
			public void fireEvent() {
				handleFileDeleted();
			}
		});

		fileSystemWatcher.addFileAddedListener(new IListener() {
			public void fireEvent() {
				handleFileAdded();
			}
		});

		fileSystemWatcher.addFileChangedListener(new IListener() {
			public void fireEvent() {
				handleFileChanged();
			}
		});
	}

	private void handleFileDeleted() {
		for (File deletedFile : fileSystemWatcher.getDeletedFiles()) {
			bundleRegistry.uninstallBundle(deletedFile);
		}
	}

	private void handleFileAdded() {
		for (File addedFile : fileSystemWatcher.getAddedFiles()) {
			bundleRegistry.installBundle(addedFile);
		}
	}

	private void handleFileChanged() {
		for (File changedFile : fileSystemWatcher.getChangedFiles()) {
			bundleRegistry.uninstallBundle(changedFile);
			bundleRegistry.installBundle(changedFile);
		}
	}
}
