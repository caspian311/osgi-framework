package net.todd.osgi.platform.core;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.todd.common.uitools.IListener;
import net.todd.osgi.platform.core.DirectoryWatcher;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class DirectoryWatcherTest {
	@Test
	public void listenersDontGetNotifiedIfNothingChangesInTheDirectory() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

			IListener listener = createMock(IListener.class);
			replay(listener);

			DirectoryWatcher watcher = new DirectoryWatcher(tempDir);
			watcher.addFileChangedListener(listener);
			watcher.checkForChanges();

			verify(listener);
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	public void listenersGetNotifiedWhenFilesGetAddedToDirectory() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

			IListener listener = createMock(IListener.class);
			listener.fireEvent();
			replay(listener);

			DirectoryWatcher watcher = new DirectoryWatcher(tempDir);
			watcher.addFileAddedListener(listener);

			String filename = UUID.randomUUID().toString();

			File newFile = new File(tempDir, filename);
			FileOutputStream fos = null;
			PrintWriter out = null;
			try {
				fos = new FileOutputStream(newFile);
				out = new PrintWriter(fos);

				out.println("hello world");
				out.flush();
			} finally {
				out.close();
				fos.close();
			}

			watcher.checkForChanges();

			verify(listener);
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	public void listenersGetNotifiedWhenAFileChangesInTheDirectory() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		String directoryLocation = tempDir.getAbsolutePath();
		FileUtils.forceDelete(tempDir);
		File directory = new File(directoryLocation);
		try {
			directory.mkdir();

			String filename = UUID.randomUUID().toString();

			File file = new File(directory, filename);
			FileOutputStream fos = null;
			PrintWriter out = null;
			try {
				fos = new FileOutputStream(file);
				out = new PrintWriter(fos);

				out.println("hello world");
				out.flush();
			} finally {
				out.close();
				fos.close();
			}

			Thread.sleep(1000);

			IListener listener = createMock(IListener.class);
			listener.fireEvent();
			replay(listener);

			DirectoryWatcher watcher = new DirectoryWatcher(directory);
			watcher.addFileChangedListener(listener);

			File sameFile = new File(directory, filename);
			try {
				fos = new FileOutputStream(sameFile);
				out = new PrintWriter(fos);

				out.println("hello again");
				out.flush();
				fos.flush();
			} finally {
				out.close();
				fos.close();
			}

			watcher.checkForChanges();

			verify(listener);
		} finally {
			FileUtils.deleteDirectory(directory);
		}
	}

	@Test
	public void listenersDoNotGetNotifiedWhenFilesChangeAfterShutdown() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

			IListener listener = createMock(IListener.class);
			replay(listener);

			DirectoryWatcher watcher = new DirectoryWatcher(tempDir);
			watcher.addFileChangedListener(listener);
			watcher.checkForChanges();

			String filename = UUID.randomUUID().toString();

			File newFile = new File(tempDir, filename);
			FileOutputStream fos = null;
			PrintWriter out = null;
			try {
				fos = new FileOutputStream(newFile);
				out = new PrintWriter(fos);

				out.println("hello world");
				out.flush();
			} finally {
				out.close();
				fos.close();
			}

			Thread.sleep(1000);

			verify(listener);

		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	public void whatChangedIsCorrectForFileAdditions() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

			DirectoryWatcher watcher = new DirectoryWatcher(tempDir);
			MockChangeFileListener fileChangedListener = new MockChangeFileListener(watcher);
			MockAddFileListener fileAddedListener = new MockAddFileListener(watcher);
			MockDeleteFileListener fileDeletedListener = new MockDeleteFileListener(watcher);
			watcher.addFileChangedListener(fileChangedListener);
			watcher.addFileAddedListener(fileAddedListener);
			watcher.addFileDeletedListener(fileDeletedListener);

			watcher.checkForChanges();

			assertNull(fileChangedListener.modifiedFiles);
			assertNull(fileAddedListener.addedFiles);
			assertNull(fileDeletedListener.deletedFiles);

			File newFile = new File(tempDir, UUID.randomUUID().toString());
			String modifiedFilePath = newFile.getAbsolutePath();
			FileOutputStream fos = null;
			PrintWriter out = null;
			try {
				fos = new FileOutputStream(newFile);
				out = new PrintWriter(fos);

				out.println("hello world");
				out.flush();
			} finally {
				out.close();
				fos.close();
			}

			watcher.checkForChanges();

			assertNull(fileChangedListener.modifiedFiles);
			assertNull(fileDeletedListener.deletedFiles);
			assertNotNull(fileAddedListener.addedFiles);
			assertEquals(1, fileAddedListener.addedFiles.size());
			assertEquals(modifiedFilePath, fileAddedListener.addedFiles.get(0).getAbsolutePath());
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	public void whatChangedIsCorrectOnFileChange() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

			File newFile = new File(tempDir, UUID.randomUUID().toString());
			String modifiedFilePath = newFile.getAbsolutePath();
			FileOutputStream fos = null;
			PrintWriter out = null;
			try {
				fos = new FileOutputStream(newFile);
				out = new PrintWriter(fos);

				out.println("hello world");
				out.flush();
			} finally {
				out.close();
				fos.close();
			}

			Thread.sleep(1000);

			DirectoryWatcher watcher = new DirectoryWatcher(tempDir);
			MockChangeFileListener fileChangedListener = new MockChangeFileListener(watcher);
			MockDeleteFileListener fileDeletedListener = new MockDeleteFileListener(watcher);
			MockAddFileListener fileAddedListener = new MockAddFileListener(watcher);
			watcher.addFileChangedListener(fileChangedListener);
			watcher.addFileDeletedListener(fileDeletedListener);
			watcher.addFileAddedListener(fileAddedListener);

			watcher.checkForChanges();

			assertNull(fileChangedListener.modifiedFiles);
			assertNull(fileAddedListener.addedFiles);
			assertNull(fileDeletedListener.deletedFiles);

			File oldFile = new File(modifiedFilePath);
			try {
				fos = new FileOutputStream(oldFile);
				out = new PrintWriter(fos);

				out.println("hello again");
				out.flush();
				fos.flush();
			} finally {
				out.close();
				fos.close();
			}

			watcher.checkForChanges();

			assertNull(fileAddedListener.addedFiles);
			assertNull(fileDeletedListener.deletedFiles);
			assertNotNull(fileChangedListener.modifiedFiles);
			assertEquals(1, fileChangedListener.modifiedFiles.size());
			assertEquals(modifiedFilePath, fileChangedListener.modifiedFiles.get(0)
					.getAbsolutePath());
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	public void whatChangedIsCorrectForFileDeletions() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

			File newFile = new File(tempDir, UUID.randomUUID().toString());
			String modifiedFilePath = newFile.getAbsolutePath();
			FileOutputStream fos = null;
			PrintWriter out = null;
			try {
				fos = new FileOutputStream(newFile);
				out = new PrintWriter(fos);

				out.println("hello world");
				out.flush();
			} finally {
				out.close();
				fos.close();
			}

			DirectoryWatcher watcher = new DirectoryWatcher(tempDir);
			MockChangeFileListener fileChangedListener = new MockChangeFileListener(watcher);
			MockAddFileListener fileAddedListener = new MockAddFileListener(watcher);
			MockDeleteFileListener fileDeletedListener = new MockDeleteFileListener(watcher);
			watcher.addFileChangedListener(fileChangedListener);
			watcher.addFileChangedListener(fileAddedListener);
			watcher.addFileDeletedListener(fileDeletedListener);

			watcher.checkForChanges();

			assertNull(fileChangedListener.modifiedFiles);
			assertNull(fileAddedListener.addedFiles);
			assertNull(fileDeletedListener.deletedFiles);

			File oldFile = new File(modifiedFilePath);
			FileUtils.forceDelete(oldFile);

			watcher.checkForChanges();

			assertNull(fileChangedListener.modifiedFiles);
			assertNull(fileAddedListener.addedFiles);
			assertNotNull(fileDeletedListener.deletedFiles);
			assertEquals(1, fileDeletedListener.deletedFiles.size());
			assertEquals(modifiedFilePath, fileDeletedListener.deletedFiles.get(0)
					.getAbsolutePath());
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	public void whatEventsAreFiredCorrectlyForFileDeletionsWhenThereAreMultipleFiles()
			throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

			File file1 = new File(tempDir, "file1");
			String file1Path = file1.getAbsolutePath();
			FileOutputStream fos = null;
			PrintWriter out = null;
			try {
				fos = new FileOutputStream(file1);
				out = new PrintWriter(fos);

				out.println("hello world");
				out.flush();
			} finally {
				out.close();
				fos.close();
			}

			File file2 = new File(tempDir, "file2");
			try {
				fos = new FileOutputStream(file2);
				out = new PrintWriter(fos);

				out.println("hello world");
				out.flush();
			} finally {
				out.close();
				fos.close();
			}

			DirectoryWatcher watcher = new DirectoryWatcher(tempDir);
			MockChangeFileListener fileChangedListener = new MockChangeFileListener(watcher);
			MockAddFileListener fileAddedListener = new MockAddFileListener(watcher);
			MockDeleteFileListener fileDeletedListener = new MockDeleteFileListener(watcher);
			watcher.addFileChangedListener(fileChangedListener);
			watcher.addFileChangedListener(fileAddedListener);
			watcher.addFileDeletedListener(fileDeletedListener);

			watcher.checkForChanges();

			assertNull(fileChangedListener.modifiedFiles);
			assertNull(fileAddedListener.addedFiles);
			assertNull(fileDeletedListener.deletedFiles);

			File fileToDelete = new File(file1Path);
			FileUtils.forceDelete(fileToDelete);

			watcher.checkForChanges();

			assertNull(fileChangedListener.modifiedFiles);
			assertNull(fileAddedListener.addedFiles);
			assertNotNull(fileDeletedListener.deletedFiles);
			assertEquals(1, fileDeletedListener.deletedFiles.size());
			assertEquals(file1Path, fileDeletedListener.deletedFiles.get(0).getAbsolutePath());
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	private static class MockAddFileListener implements IListener {
		private final DirectoryWatcher watcher;
		private List<File> addedFiles;

		public MockAddFileListener(DirectoryWatcher watcher) {
			this.watcher = watcher;
		}

		public void fireEvent() {
			addedFiles = new ArrayList<File>(watcher.getAddedFiles());
		}
	}

	private static class MockDeleteFileListener implements IListener {
		private final DirectoryWatcher watcher;
		private List<File> deletedFiles;

		public MockDeleteFileListener(DirectoryWatcher watcher) {
			this.watcher = watcher;
		}

		public void fireEvent() {
			deletedFiles = new ArrayList<File>(watcher.getDeletedFiles());
		}
	}

	private static class MockChangeFileListener implements IListener {
		private final DirectoryWatcher watcher;
		private List<File> modifiedFiles;

		public MockChangeFileListener(DirectoryWatcher watcher) {
			this.watcher = watcher;
		}

		public void fireEvent() {
			modifiedFiles = new ArrayList<File>(watcher.getChangedFiles());
		}
	}
}
