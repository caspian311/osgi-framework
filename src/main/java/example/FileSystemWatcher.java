package example;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.todd.common.uitools.IListener;
import net.todd.common.uitools.ListenerManager;

public class FileSystemWatcher implements IFileSystemWatcher {
	private final File watchedDirectory;
	private final long sleeptime;
	private Thread thread;
	private Map<String, Long> previousLastModifiedMap;

	private final List<File> addedDifferences = new ArrayList<File>();
	private final List<File> deletedDifferences = new ArrayList<File>();
	private final List<File> changedDifferences = new ArrayList<File>();

	private final ListenerManager fileChangedListenerManager = new ListenerManager();
	private final ListenerManager fileAddedListenerManager = new ListenerManager();
	private final ListenerManager fileDeletedListenerManager = new ListenerManager();

	public FileSystemWatcher(File pluginDirectory, long sleeptime) {
		this.watchedDirectory = pluginDirectory;
		this.sleeptime = sleeptime;
	}

	public void startup() throws Exception {
		previousLastModifiedMap = new HashMap<String, Long>();
		collectLastModifieds(watchedDirectory, previousLastModifiedMap);

		thread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					Map<String, Long> currentLastModifiedMap = new HashMap<String, Long>();
					collectLastModifieds(watchedDirectory, currentLastModifiedMap);

					findDirectoryDifferences(previousLastModifiedMap, currentLastModifiedMap);

					if (!changedDifferences.isEmpty()) {
						fileChangedListenerManager.notifyListeners();
					}
					if (!addedDifferences.isEmpty()) {
						fileAddedListenerManager.notifyListeners();
					}
					if (!deletedDifferences.isEmpty()) {
						fileDeletedListenerManager.notifyListeners();
					}

					previousLastModifiedMap = currentLastModifiedMap;

					try {
						Thread.sleep(sleeptime);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}, "File System Watcher");
		thread.setDaemon(true);
		thread.start();
	}

	private void collectLastModifieds(File directory, Map<String, Long> lastModifiedMap) {
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					collectLastModifieds(file, lastModifiedMap);
				} else {
					lastModifiedMap.put(file.getAbsolutePath(), file.lastModified());
				}
			}
		}
	}

	private void findDirectoryDifferences(Map<String, Long> previousLastModifiedMap,
			Map<String, Long> currentLastModifiedMap) {
		changedDifferences.clear();
		addedDifferences.clear();
		deletedDifferences.clear();

		List<String> previousFileList = new ArrayList<String>(previousLastModifiedMap.keySet());
		Collections.sort(previousFileList);

		List<String> currentFileList = new ArrayList<String>(currentLastModifiedMap.keySet());
		Collections.sort(currentFileList);

		for (int i = 0; i < previousFileList.size(); i++) {
			String previousPath = previousFileList.get(i);
			if (currentFileList.size() > i) {
				String currentPath = currentFileList.get(i);
				if (previousPath.equals(currentPath)) {
					if (previousLastModifiedMap.get(previousPath).equals(
							currentLastModifiedMap.get(currentPath))) {
						continue;
					} else {
						changedDifferences.add(new File(currentPath));
					}
				} else {
					if (currentFileList.contains(previousPath)) {
						addedDifferences.add(new File(currentPath));
					} else {
						deletedDifferences.add(new File(previousPath));
					}
				}
			} else {
				deletedDifferences.add(new File(previousPath));
			}
		}

		if (currentFileList.size() > previousFileList.size()) {
			for (int i = previousFileList.size(); i < currentFileList.size(); i++) {
				addedDifferences.add(new File(currentFileList.get(i)));
			}
		}
	}

	public void shutdown() throws Exception {
		thread.interrupt();
		thread.join();
	}

	public void addFileChangedListener(IListener listener) {
		fileChangedListenerManager.addListener(listener);
	}

	public void addFileAddedListener(IListener listener) {
		fileAddedListenerManager.addListener(listener);
	}

	public void addFileDeletedListener(IListener listener) {
		fileDeletedListenerManager.addListener(listener);
	}

	public List<File> getDeletedFiles() {
		return deletedDifferences;
	}

	public List<File> getAddedFiles() {
		return addedDifferences;
	}

	public List<File> getChangedFiles() {
		return changedDifferences;
	}
}
