package example;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.todd.common.uitools.IListener;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ServiceManagerTest {
	@Test
	public void serviceManagerUninstallsBundleOfCorespondingDeletedJarFile() throws Exception {
		File deletedFile = File.createTempFile(getClass().getName(), ".jar");
		FileUtils.forceDelete(deletedFile);

		MockFileSystemWatcher fileSystemWatcher = new MockFileSystemWatcher();
		fileSystemWatcher.deletedFiles.add(deletedFile);

		IBundleRegistry bundleRegistry = createMock(IBundleRegistry.class);

		bundleRegistry.uninstallBundle(deletedFile);

		replay(bundleRegistry);

		new ServiceManager(bundleRegistry, fileSystemWatcher);
		fileSystemWatcher.fileDeletedListener.fireEvent();

		verify(bundleRegistry);
	}

	@Test
	public void serviceManagerDoesntDieWhenUninstallThrowsException() throws Exception {
		File deletedFile = File.createTempFile(getClass().getName(), ".jar");
		FileUtils.forceDelete(deletedFile);

		MockFileSystemWatcher fileSystemWatcher = new MockFileSystemWatcher();
		fileSystemWatcher.deletedFiles.add(deletedFile);

		IBundleRegistry bundleRegistry = createMock(IBundleRegistry.class);

		bundleRegistry.uninstallBundle(deletedFile);
		String errorMessage = UUID.randomUUID().toString();
		expectLastCall().andThrow(new BundleRegistryException(errorMessage));

		replay(bundleRegistry);

		new ServiceManager(bundleRegistry, fileSystemWatcher);

		try {
			fileSystemWatcher.fileDeletedListener.fireEvent();
			fail("Should have thrown a BundleRegistryException");
		} catch (BundleRegistryException e) {
			assertEquals(errorMessage, e.getMessage());
		}

		verify(bundleRegistry);
	}

	@Test
	public void serviceManagerInstallsBundleOfCorespondingAddedJarFile() throws Exception {
		File addedFile = File.createTempFile(getClass().getName(), ".jar");
		FileUtils.forceDelete(addedFile);

		MockFileSystemWatcher fileSystemWatcher = new MockFileSystemWatcher();
		fileSystemWatcher.addedFiles.add(addedFile);

		IBundleRegistry bundleRegistry = createMock(IBundleRegistry.class);

		bundleRegistry.installBundle(addedFile);

		replay(bundleRegistry);

		new ServiceManager(bundleRegistry, fileSystemWatcher);
		fileSystemWatcher.fileAddedListener.fireEvent();

		verify(bundleRegistry);
	}

	@Test
	public void serviceManagerDoesntBlowUpWhenInstallsBundleThrowsException() throws Exception {
		File addedFile = File.createTempFile(getClass().getName(), ".jar");
		FileUtils.forceDelete(addedFile);

		MockFileSystemWatcher fileSystemWatcher = new MockFileSystemWatcher();
		fileSystemWatcher.addedFiles.add(addedFile);

		IBundleRegistry bundleRegistry = createMock(IBundleRegistry.class);

		bundleRegistry.installBundle(addedFile);
		String errorMessage = UUID.randomUUID().toString();
		expectLastCall().andThrow(new BundleRegistryException(errorMessage));

		replay(bundleRegistry);

		new ServiceManager(bundleRegistry, fileSystemWatcher);
		try {
			fileSystemWatcher.fileAddedListener.fireEvent();
			fail("Should have thrown a BundleRegistryException");
		} catch (BundleRegistryException e) {
			assertEquals(errorMessage, e.getMessage());
		}

		verify(bundleRegistry);
	}

	@Test
	public void serviceManagerReinstallsBundleOfCorespondingChangedJarFile() throws Exception {
		File changedFile = File.createTempFile(getClass().getName(), ".jar");
		FileUtils.forceDelete(changedFile);

		MockFileSystemWatcher fileSystemWatcher = new MockFileSystemWatcher();
		fileSystemWatcher.changedFiles.add(changedFile);

		IBundleRegistry bundleRegistry = createMock(IBundleRegistry.class);

		bundleRegistry.uninstallBundle(changedFile);
		bundleRegistry.installBundle(changedFile);

		replay(bundleRegistry);

		new ServiceManager(bundleRegistry, fileSystemWatcher);
		fileSystemWatcher.fileChangedListener.fireEvent();

		verify(bundleRegistry);
	}

	private static class MockFileSystemWatcher implements IDirectoryWatcher {
		private IListener fileDeletedListener;
		private IListener fileAddedListener;
		private final List<File> deletedFiles = new ArrayList<File>();
		private final List<File> addedFiles = new ArrayList<File>();
		private final List<File> changedFiles = new ArrayList<File>();
		private IListener fileChangedListener;

		public void addFileDeletedListener(IListener listener) {
			fileDeletedListener = listener;
		}

		public List<File> getDeletedFiles() {
			return deletedFiles;
		}

		public void addFileAddedListener(IListener listener) {
			fileAddedListener = listener;
		}

		public List<File> getAddedFiles() {
			return addedFiles;
		}

		public void addFileChangedListener(IListener listener) {
			this.fileChangedListener = listener;
		}

		public List<File> getChangedFiles() {
			return changedFiles;
		}

		public void checkForChanges() throws Exception {
			throw new UnsupportedOperationException();
		}
	}
}
