package net.todd.osgi.platform.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.todd.common.uitools.IListener;
import net.todd.common.uitools.ListenerManager;
import net.todd.osgi.platform.IDirectoryWatcher;

public class DirectoryWatcher implements IDirectoryWatcher {
	private final File watchedDirectory;
	private Map<String, Long> previousLastModifiedMap;

	private final List<File> addedDifferences = new ArrayList<File>();
	private final List<File> deletedDifferences = new ArrayList<File>();
	private final List<File> changedDifferences = new ArrayList<File>();

	private final ListenerManager fileChangedListenerManager = new ListenerManager();
	private final ListenerManager fileAddedListenerManager = new ListenerManager();
	private final ListenerManager fileDeletedListenerManager = new ListenerManager();

	public DirectoryWatcher(File pluginDirectory) {
		this.watchedDirectory = pluginDirectory;

		previousLastModifiedMap = new HashMap<String, Long>();
		collectLastModifieds(watchedDirectory, previousLastModifiedMap);
	}

	public void checkForChanges() throws Exception {
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
	}

	private void collectLastModifieds(File directory, Map<String, Long> lastModifiedMap) {
		String[] filenames = directory.list();
		if (filenames != null && filenames.length != 0) {
			for (String filename : filenames) {
				File file = new File(directory, filename);
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

		Set<String> previousFiles = previousLastModifiedMap.keySet();
		Set<String> currentFiles = currentLastModifiedMap.keySet();

		for (String previousPath : previousFiles) {
			if (!currentFiles.contains(previousPath)) {
				deletedDifferences.add(new File(previousPath));
			} else {
				if (!previousLastModifiedMap.get(previousPath).equals(
						currentLastModifiedMap.get(previousPath))) {
					changedDifferences.add(new File(previousPath));
				}
			}
		}

		for (String currentPath : currentFiles) {
			if (!previousFiles.contains(currentPath)) {
				addedDifferences.add(new File(currentPath));
			}
		}
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
