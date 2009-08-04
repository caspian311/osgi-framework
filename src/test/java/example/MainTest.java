package example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;

public class MainTest {
	@Test
	public void ifNullIsPassedInForArgsAnIllegalStateExceptionIsThrown() {
		try {
			Main.main(null);
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
			assertEquals("Need a plugin directory", e.getMessage());
		}
	}

	@Test
	public void ifNoArgsAreGivenAnIllegalStateExceptionIsThrown() {
		try {
			Main.main(new String[] {});
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
			assertEquals("Need a plugin directory", e.getMessage());
		}
	}

	@Test
	public void noExceptionIsThrownIfGoodArgumentIsPassedIn() {
		String pluginDirectoryLocation = UUID.randomUUID().toString();
		Main.main(new String[] { pluginDirectoryLocation });
	}
}
