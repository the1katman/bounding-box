package test.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtil {

    private static final Path TEST_FILES_BASE_DIRECTORY = Paths.get("test/data/");

    public static Path getPath(final Class<?> testClass, final String fileName) throws URISyntaxException {
        final ClassLoader classLoader = testClass.getClassLoader();
        final String path = TEST_FILES_BASE_DIRECTORY.resolve(fileName).toString();
        final URL fileResourceURL = classLoader.getResource(path);
        @SuppressWarnings("DataFlowIssue") final URI fileResourceURI = fileResourceURL.toURI();
        return Paths.get(fileResourceURI);
    }

}
