package example;

import java.io.File;
import java.util.List;

import net.todd.common.uitools.IListener;

public interface IFileSystemWatcher {
	void startup() throws Exception;

	void shutdown() throws Exception;

	void addFileChangedListener(IListener listener);

	void addFileAddedListener(IListener listener);

	void addFileDeletedListener(IListener listener);

	public List<File> getDeletedFiles();
}
