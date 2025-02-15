package test;

import com.the1katman.BoundingBox;
import com.the1katman.BoundingBoxApp;
import com.the1katman.Coordinate;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import test.util.TestUtil;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class BoundingBoxAppTests {

    private final Class<? extends BoundingBoxAppTests> TEST_CLASS = getClass();

    @DataProvider(name = "validGrids")
    public Object[][] createValidGrids() {
        return new Object[][]{
                createValidGrid("single_coordinate_no_box_grid.txt", new int[][]{}),
                createValidGrid("no_boxes_grid.txt", new int[][]{}),
                createValidGrid("single_coordinate_single_box_grid.txt", new int[][]{{1, 1, 1, 1}}),
                createValidGrid("basic_grid.txt", new int[][]{{2, 2, 3, 3}}),
                createValidGrid("basic_overlap_boxes_grid.txt", new int[][]{{1, 1, 2, 2}}),
                createValidGrid("detached_boxes_grid.txt", new int[][]{{2, 3, 2, 4}}),
                createValidGrid("vertical_box_grid.txt", new int[][]{{1, 2, 4, 2}}),
                createValidGrid("single_box_entire_grid.txt", new int[][]{{1, 1, 5, 5}}),
                createValidGrid("single_box_grid.txt", new int[][]{{2, 2, 4, 4}}),
                createValidGrid("single_box_grid_2.txt", new int[][]{{2, 2, 4, 4}}),
                createValidGrid("single_box_grid_3.txt", new int[][]{{2, 2, 4, 4}}),
                createValidGrid("single_box_grid_4.txt", new int[][]{{2, 2, 4, 4}}),
                createValidGrid("multiple_single_boxes_grid.txt", new int[][]{
                        {2, 1, 2, 1},
                        {2, 7, 2, 7}
                }),
                createValidGrid("multiple_single_boxes_grid_2.txt", new int[][]{
                        {1, 1, 1, 1},
                        {2, 3, 2, 3},
                        {3, 4, 3, 4},
                        {4, 5, 4, 5},
                        {5, 6, 5, 6}
                }),
                createValidGrid("multiple_boxes_grid.txt", new int[][]{
                        {2, 2, 4, 4},
                        {2, 8, 4, 10}
                }),
                createValidGrid("multiple_boxes_grid_2.txt", new int[][]{
                        {2, 2, 3, 3},
                        {5, 3, 6, 4},
                        {2, 6, 3, 7}
                }),
                createValidGrid("multiple_detached_boxes_grid.txt", new int[][]{
                        {1, 2, 4, 4},
                        {1, 7, 4, 9}
                }),
                createValidGrid("multiple_overlap_boxes_grid.txt", new int[][]{
                        {1, 1, 3, 3},
                        {6, 12, 8, 14}
                })
        };
    }

    private static Object[] createValidGrid(final String fileName, final int[][] coordinates) {
        final HashSet<BoundingBox> boundingBoxes = new HashSet<>();
        for (final int[] coordinate : coordinates) {
            final int minRow = coordinate[0];
            final int minColumn = coordinate[1];
            final Coordinate minCoordinate = new Coordinate(minRow, minColumn);

            final int maxRow = coordinate[2];
            final int maxColumn = coordinate[3];
            final Coordinate maxCoordinate = new Coordinate(maxRow, maxColumn);

            final BoundingBox boundingBox = new BoundingBox(minCoordinate, maxCoordinate);
            boundingBoxes.add(boundingBox);
        }
        return new Object[]{fileName, boundingBoxes};
    }

    @Test(dataProvider = "validGrids")
    public void testValidBoundingBox(final String fileName, final Set<BoundingBox> expectedBoundingBoxes) throws URISyntaxException {
        final Path inputPath = TestUtil.getPath(TEST_CLASS, fileName);
        final Set<BoundingBox> actualBoundingBoxes = BoundingBoxApp.findLargestSeparateBoundingBoxes(inputPath);

        assertThat(actualBoundingBoxes)
                .as("The actual bounding boxes do not match the expected bounding boxes.")
                .containsExactlyInAnyOrderElementsOf(expectedBoundingBoxes);
    }

}
