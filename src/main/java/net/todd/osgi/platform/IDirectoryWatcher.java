package net.todd.osgi.platform;

import java.io.File;
import java.util.List;

import net.todd.common.uitools.IListener;

public interface IDirectoryWatcher {
	void checkForChanges() throws Exception;

	void addFileChangedListener(IListener listener);

	void addFileAddedListener(IListener listener);

	void addFileDeletedListener(IListener listener);

	public List<File> getDeletedFiles();

	List<File> getAddedFiles();

	List<File> getChangedFiles();
}
