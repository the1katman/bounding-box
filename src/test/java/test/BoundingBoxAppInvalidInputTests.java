package test;

import com.the1katman.BoundingBoxApp;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import test.util.TestUtil;

import java.net.URISyntaxException;
import java.nio.file.Path;

import static com.the1katman.BoundingBoxApp.VALID_GRID_REQUIRED_MESSAGE;
import static com.the1katman.BoundingBoxApp.VALID_INPUT_FILE_REQUIRED_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Test
public class BoundingBoxAppInvalidInputTests {

    private final Class<? extends BoundingBoxAppInvalidInputTests> TEST_CLASS = getClass();

    @DataProvider(name = "invalidArguments")
    public Object[][] createInvalidArguments() {
        return new Object[][]{
                {new String[]{}},
                {new String[]{"input_file1.txt", "input_file2.txt"}},
                {new String[]{"file_does_not_exist.txt"}}
        };
    }

    @Test(dataProvider = "invalidArguments")
    public void testInvalidArguments(final String[] args) {
        assertThatThrownBy(() -> BoundingBoxApp.getInputPath(args))
                .as("The expected exception for an invalid argument was not thrown.")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(VALID_INPUT_FILE_REQUIRED_MESSAGE);
    }

    @DataProvider(name = "invalidGrids")
    public Object[][] createInvalidGrids() {
        return new Object[][]{
                {"empty_grid.txt"},
                {"different_line_lengths_grid.txt"},
                {"invalid_characters_grid.txt"},
                {"newline_inside_grid.txt"},
                {"no_newline_at_end_of_file_grid.txt"}
        };
    }

    @Test(dataProvider = "invalidGrids")
    public void testInvalidGrid(final String fileName) throws URISyntaxException {
        final Path inputPath = TestUtil.getPath(TEST_CLASS, fileName);
        assertThatThrownBy(() -> BoundingBoxApp.findLargestSeparateBoundingBoxes(inputPath))
                .as("The expected exception for an invalid grid was not thrown.")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(VALID_GRID_REQUIRED_MESSAGE);
    }

}
