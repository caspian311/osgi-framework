package example;

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

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

public class FileSystemWatcherTest {
	@Test
	@Ignore
	public void listenersDontGetNotifiedIfNothingChangesInTheDirectory() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

			IListener listener = createMock(IListener.class);
			replay(listener);

			FileSystemWatcher watcher = new FileSystemWatcher(tempDir, 50);
			watcher.addFileChangedListener(listener);
			watcher.startup();
			Thread.sleep(200);

			verify(listener);

			watcher.shutdown();
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	@Ignore
	public void listenersGetNotifiedWhenFilesGetAddedToDirectory() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

			IListener listener = createMock(IListener.class);
			listener.fireEvent();
			replay(listener);

			FileSystemWatcher watcher = new FileSystemWatcher(tempDir, 50);
			watcher.addFileAddedListener(listener);
			watcher.startup();

			Thread.sleep(200);

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

			Thread.sleep(200);

			verify(listener);

			watcher.shutdown();
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	public void listenersGetNotifiedWhenFilesChangeInTheDirectory() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

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

			IListener listener = createMock(IListener.class);
			listener.fireEvent();
			replay(listener);

			FileSystemWatcher watcher = new FileSystemWatcher(tempDir, 50);
			watcher.addFileChangedListener(listener);
			watcher.startup();

			Thread.sleep(200);

			File oldFile = new File(tempDir, filename);
			try {
				fos = new FileOutputStream(oldFile);
				out = new PrintWriter(fos);

				out.println("hello again");
				out.flush();
			} finally {
				out.close();
				fos.close();
			}

			Thread.sleep(200);

			verify(listener);

			watcher.shutdown();
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	@Ignore
	public void listenersDoNotGetNotifiedWhenFilesChangeAfterShutdown() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

			IListener listener = createMock(IListener.class);
			replay(listener);

			FileSystemWatcher watcher = new FileSystemWatcher(tempDir, 50);
			watcher.addFileChangedListener(listener);
			watcher.startup();

			Thread.sleep(200);
			watcher.shutdown();

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

			Thread.sleep(200);

			verify(listener);

		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	@Ignore
	public void whatChangedIsCorrectForFileAdditions() throws Exception {
		File tempDir = File.createTempFile(getClass().getName(), null);
		tempDir.delete();
		try {
			tempDir.mkdir();

			FileSystemWatcher watcher = new FileSystemWatcher(tempDir, 50);
			MockChangeFileListener fileChangedListener = new MockChangeFileListener(watcher);
			MockAddFileListener fileAddedListener = new MockAddFileListener(watcher);
			MockDeleteFileListener fileDeletedListener = new MockDeleteFileListener(watcher);
			watcher.addFileChangedListener(fileChangedListener);
			watcher.addFileAddedListener(fileAddedListener);
			watcher.addFileDeletedListener(fileDeletedListener);
			watcher.startup();

			Thread.sleep(200);

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

			Thread.sleep(200);

			assertNull(fileChangedListener.modifiedFiles);
			assertNull(fileDeletedListener.deletedFiles);
			assertNotNull(fileAddedListener.addedFiles);
			assertEquals(1, fileAddedListener.addedFiles.size());
			assertEquals(modifiedFilePath, fileAddedListener.addedFiles.get(0).getAbsolutePath());

			watcher.shutdown();
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

			FileSystemWatcher watcher = new FileSystemWatcher(tempDir, 50);
			MockChangeFileListener fileChangedListener = new MockChangeFileListener(watcher);
			MockDeleteFileListener fileDeletedListener = new MockDeleteFileListener(watcher);
			MockAddFileListener fileAddedListener = new MockAddFileListener(watcher);
			watcher.addFileChangedListener(fileChangedListener);
			watcher.addFileDeletedListener(fileDeletedListener);
			watcher.addFileAddedListener(fileAddedListener);
			watcher.startup();

			Thread.sleep(200);

			assertNull(fileChangedListener.modifiedFiles);
			assertNull(fileAddedListener.addedFiles);
			assertNull(fileDeletedListener.deletedFiles);

			File oldFile = new File(modifiedFilePath);
			try {
				fos = new FileOutputStream(oldFile);
				out = new PrintWriter(fos);

				out.println("hello again");
				out.flush();
			} finally {
				out.close();
				fos.close();
			}

			Thread.sleep(200);

			assertNull(fileAddedListener.addedFiles);
			assertNull(fileDeletedListener.deletedFiles);
			assertNotNull(fileChangedListener.modifiedFiles);
			assertEquals(1, fileChangedListener.modifiedFiles.size());
			assertEquals(modifiedFilePath, fileChangedListener.modifiedFiles.get(0)
					.getAbsolutePath());

			watcher.shutdown();
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	@Ignore
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

			FileSystemWatcher watcher = new FileSystemWatcher(tempDir, 50);
			MockChangeFileListener fileChangedListener = new MockChangeFileListener(watcher);
			MockAddFileListener fileAddedListener = new MockAddFileListener(watcher);
			MockDeleteFileListener fileDeletedListener = new MockDeleteFileListener(watcher);
			watcher.addFileChangedListener(fileChangedListener);
			watcher.addFileChangedListener(fileAddedListener);
			watcher.addFileDeletedListener(fileDeletedListener);
			watcher.startup();

			Thread.sleep(200);

			assertNull(fileChangedListener.modifiedFiles);
			assertNull(fileAddedListener.addedFiles);
			assertNull(fileDeletedListener.deletedFiles);

			File oldFile = new File(modifiedFilePath);
			FileUtils.forceDelete(oldFile);

			Thread.sleep(200);

			assertNull(fileChangedListener.modifiedFiles);
			assertNull(fileAddedListener.addedFiles);
			assertNotNull(fileDeletedListener.deletedFiles);
			assertEquals(1, fileDeletedListener.deletedFiles.size());
			assertEquals(modifiedFilePath, fileDeletedListener.deletedFiles.get(0)
					.getAbsolutePath());

			watcher.shutdown();
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	private static class MockAddFileListener implements IListener {
		private final FileSystemWatcher watcher;
		private List<File> addedFiles;

		public MockAddFileListener(FileSystemWatcher watcher) {
			this.watcher = watcher;
		}

		public void fireEvent() {
			addedFiles = new ArrayList<File>(watcher.getAddedFiles());
		}
	}

	private static class MockDeleteFileListener implements IListener {
		private final FileSystemWatcher watcher;
		private List<File> deletedFiles;

		public MockDeleteFileListener(FileSystemWatcher watcher) {
			this.watcher = watcher;
		}

		public void fireEvent() {
			deletedFiles = new ArrayList<File>(watcher.getDeletedFiles());
		}
	}

	private static class MockChangeFileListener implements IListener {
		private final FileSystemWatcher watcher;
		private List<File> modifiedFiles;

		public MockChangeFileListener(FileSystemWatcher watcher) {
			this.watcher = watcher;
		}

		public void fireEvent() {
			modifiedFiles = new ArrayList<File>(watcher.getChangedFiles());
		}
	}
}
