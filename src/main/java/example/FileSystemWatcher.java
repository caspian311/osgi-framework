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
	private Map<String, Long> previousLastModifiedMap;

	private final List<File> addedDifferences = new ArrayList<File>();
	private final List<File> deletedDifferences = new ArrayList<File>();
	private final List<File> changedDifferences = new ArrayList<File>();

	private final ListenerManager fileChangedListenerManager = new ListenerManager();
	private final ListenerManager fileAddedListenerManager = new ListenerManager();
	private final ListenerManager fileDeletedListenerManager = new ListenerManager();

	public FileSystemWatcher(File pluginDirectory) {
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
				System.out.println(file.getAbsolutePath() + " last modified at "
						+ file.lastModified());
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
