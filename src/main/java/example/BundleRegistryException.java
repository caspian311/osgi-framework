package example;

public class BundleRegistryException extends RuntimeException {
	private static final long serialVersionUID = -6145566197114733904L;

	public BundleRegistryException(String message) {
		super(message);
	}

	public BundleRegistryException(Throwable cause) {
		super(cause);
	}

	public BundleRegistryException(String message, Throwable cause) {
		super(message, cause);
	}
}
