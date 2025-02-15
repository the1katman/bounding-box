package com.the1katman;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoundingBoxApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoundingBoxApp.class);

    public static final String VALID_INPUT_FILE_REQUIRED_MESSAGE = "Please provide exactly one existing input file.";

    public static final String VALID_GRID_REQUIRED_MESSAGE = "Please provide an input file that consists of lines of equal length, containing only hyphens (\"-\") and asterisks (\"*\"), with a newline at the end of the file.";

    static final String BOUNDING_BOX_FORMAT = "(%d,%d)(%d,%d)";

    private static final String INPUT_FILE_VALID_LINE_REGEX = "^[\\-*\\n]+$";
    private static final Pattern INPUT_FILE_LINE_VALIDATION_PATTERN = Pattern.compile(INPUT_FILE_VALID_LINE_REGEX);

    public static void main(final String[] args) {
        Set<BoundingBox> largestSeparateBoundingBoxes = null;

        try {
            final Path inputPath = getInputPath(args);
            largestSeparateBoundingBoxes = findLargestSeparateBoundingBoxes(inputPath);
        } catch (final IllegalArgumentException e) {
            final String message = e.getMessage();
            LOGGER.error(message);
            System.exit(1);
        }

        logBoundingBoxes(largestSeparateBoundingBoxes);
    }

    public static Path getInputPath(final String[] args) {
        Path inputPath = null;

        boolean isValidInputPath = false;
        if (args.length == 1) {
            final String inputPathString = args[0];
            inputPath = Paths.get(inputPathString);
            isValidInputPath = Files.exists(inputPath);
        }

        if (!isValidInputPath) {
            throw new IllegalArgumentException(VALID_INPUT_FILE_REQUIRED_MESSAGE);
        }

        return inputPath;
    }

    public static Set<BoundingBox> findLargestSeparateBoundingBoxes(final Path inputPath) {
        final BoundingBoxGrid boundingBoxGrid = getBoundingBoxGrid(inputPath);
        final Set<BoundingBox> separateBoundingBoxes = findSeparateBoundingBoxes(boundingBoxGrid);
        return getLargestBoundingBoxes(separateBoundingBoxes);
    }

    private static BoundingBoxGrid getBoundingBoxGrid(final Path inputPath) {
        final BoundingBoxGrid boundingBoxGrid = new BoundingBoxGrid();

        long readFileSize = 0;
        int currentRow = 1;
        try (final BufferedReader reader = Files.newBufferedReader(inputPath)) {
            String line;
            Integer expectedLineLength = null;
            while ((line = reader.readLine()) != null) {
                final int lineLength = line.length();
                readFileSize += lineLength + 1;
                if (expectedLineLength == null) {
                    expectedLineLength = lineLength;
                }
                assertLine(line, expectedLineLength);

                processLine(line, currentRow++, boundingBoxGrid);
            }
        } catch (final IOException e) {
            final String message = String.format("Error reading file: %s", inputPath);
            throw new RuntimeException(message, e);
        }

        assertProcessedInputFile(currentRow, inputPath, readFileSize);

        return boundingBoxGrid;
    }

    private static void assertLine(final String line, final Integer expectedLineLength) {
        boolean isValidLine = false;

        final int lineLength = line.length();
        if (lineLength == expectedLineLength) {
            final Matcher matcher = INPUT_FILE_LINE_VALIDATION_PATTERN.matcher(line);
            isValidLine = matcher.matches();
        }

        if (!isValidLine) {
            throwInvalidGridException();
        }
    }

    private static void throwInvalidGridException() {
        throw new IllegalArgumentException(VALID_GRID_REQUIRED_MESSAGE);
    }

    private static void processLine(final String line, final int row, final BoundingBoxGrid boundingBoxGrid) {
        final int lineLength = line.length();
        for (int i = 0; i < lineLength; i++) {
            final char c = line.charAt(i);
            if (c == '*') {
                final int column = i + 1;
                final Coordinate coordinate = new Coordinate(row, column);
                boundingBoxGrid.markCoordinate(coordinate);
            }
        }
    }

    private static void assertProcessedInputFile(final int currentRow, final Path inputPath, final long readFileSize) {
        boolean isValid = false;

        if (currentRow > 1) {
            final long expectedFileSize;
            try {
                expectedFileSize = Files.size(inputPath);
            } catch (final IOException e) {
                final String message = String.format("Error reading file size: %s", inputPath);
                throw new RuntimeException(message, e);
            }

            isValid = readFileSize == expectedFileSize;
        }

        if (!isValid) {
            throwInvalidGridException();
        }
    }

    private static Set<BoundingBox> findSeparateBoundingBoxes(final BoundingBoxGrid boundingBoxGrid) {
        Set<BoundingBox> separateBoundingBoxes = null;

        final Set<BoundingBox> boundingBoxes = findBoundingBoxes(boundingBoxGrid);
        if (boundingBoxes != null) {
            separateBoundingBoxes = new HashSet<>();

            for (final BoundingBox boundingBox : boundingBoxes) {
                final Set<BoundingBox> overlappingBoundingBoxes = boundingBoxGrid.markBoundingBox(boundingBox);
                if (overlappingBoundingBoxes == null || overlappingBoundingBoxes.isEmpty()) {
                    separateBoundingBoxes.add(boundingBox);
                } else {
                    separateBoundingBoxes.removeAll(overlappingBoundingBoxes);
                }
            }
        }

        return separateBoundingBoxes;
    }

    private static Set<BoundingBox> findBoundingBoxes(final BoundingBoxGrid boundingBoxGrid) {
        Set<BoundingBox> boundingBoxes = null;

        final Set<Coordinate> coordinates = boundingBoxGrid.getCoordinates();
        if (!coordinates.isEmpty()) {
            boundingBoxes = new HashSet<>();

            for (final Coordinate coordinate : coordinates) {
                BoundingBox boundingBox = boundingBoxGrid.getBoundingBox(coordinate);
                if (boundingBox == null) {
                    boundingBox = new BoundingBox(coordinate, coordinate);
                    findBoundingBox(boundingBoxGrid, coordinate, boundingBox);
                    boundingBoxes.add(boundingBox);
                }
            }
        }

        return boundingBoxes;
    }

    private static void findBoundingBox(final BoundingBoxGrid boundingBoxGrid, final Coordinate coordinate, final BoundingBox boundingBox) {
        boundingBoxGrid.markBoundingBox(coordinate, boundingBox);

        final int currentRow = coordinate.getRow();
        final int currentColumn = coordinate.getColumn();

        findBoundingBoxInCoordinateAbove(boundingBoxGrid,
                boundingBox,
                currentRow,
                currentColumn);
        findBoundingBoxInCoordinateToRight(boundingBoxGrid,
                boundingBox,
                currentRow,
                currentColumn);
        findBoundingBoxInCoordinateBelow(boundingBoxGrid,
                boundingBox,
                currentRow,
                currentColumn);
        findBoundingBoxInCoordinateToLeft(boundingBoxGrid,
                boundingBox,
                currentRow,
                currentColumn);
    }

    private static void findBoundingBoxInCoordinateAbove(final BoundingBoxGrid boundingBoxGrid, final BoundingBox boundingBox, final int currentRow, final int currentColumn) {
        final int rowAbove = currentRow - 1;
        final Coordinate coordinateAbove = new Coordinate(rowAbove, currentColumn);
        if (boundingBoxGrid.hasCoordinate(coordinateAbove)) {
            final BoundingBox coordinateAboveBoundingBox = boundingBoxGrid.getBoundingBox(coordinateAbove);
            if (coordinateAboveBoundingBox == null) {
                final Coordinate boundingBoxMinCoordinate = boundingBox.getMinCoordinate();
                final int boundingBoxMinRow = boundingBoxMinCoordinate.getRow();
                final int minRow = Math.min(rowAbove, boundingBoxMinRow);
                final int minColumn = boundingBoxMinCoordinate.getColumn();
                final Coordinate minCoordinate = new Coordinate(minRow, minColumn);
                boundingBox.setMinCoordinate(minCoordinate);

                findBoundingBox(boundingBoxGrid, coordinateAbove, boundingBox);
            }
        }
    }

    private static void findBoundingBoxInCoordinateToRight(final BoundingBoxGrid boundingBoxGrid, final BoundingBox boundingBox, final int currentRow, final int currentColumn) {
        final int columnToRight = currentColumn + 1;
        final Coordinate coordinateToRight = new Coordinate(currentRow, columnToRight);
        if (boundingBoxGrid.hasCoordinate(coordinateToRight)) {
            final BoundingBox coordinateToRightBoundingBox = boundingBoxGrid.getBoundingBox(coordinateToRight);
            if (coordinateToRightBoundingBox == null) {
                final Coordinate boundingBoxMaxCoordinate = boundingBox.getMaxCoordinate();
                final int maxRow = boundingBoxMaxCoordinate.getRow();
                final int boundingBoxMaxColumn = boundingBoxMaxCoordinate.getColumn();
                final int maxColumn = Math.max(columnToRight, boundingBoxMaxColumn);
                final Coordinate maxCoordinate = new Coordinate(maxRow, maxColumn);
                boundingBox.setMaxCoordinate(maxCoordinate);

                findBoundingBox(boundingBoxGrid, coordinateToRight, boundingBox);
            }
        }
    }

    private static void findBoundingBoxInCoordinateBelow(final BoundingBoxGrid boundingBoxGrid, final BoundingBox boundingBox, final int currentRow, final int currentColumn) {
        final int rowBelow = currentRow + 1;
        final Coordinate coordinateBelow = new Coordinate(rowBelow, currentColumn);
        if (boundingBoxGrid.hasCoordinate(coordinateBelow)) {
            final BoundingBox coordinateBelowBoundingBox = boundingBoxGrid.getBoundingBox(coordinateBelow);
            if (coordinateBelowBoundingBox == null) {
                final Coordinate boundingBoxMaxCoordinate = boundingBox.getMaxCoordinate();
                final int boundingBoxMaxRow = boundingBoxMaxCoordinate.getRow();
                final int maxRow = Math.max(rowBelow, boundingBoxMaxRow);
                final int maxColumn = boundingBoxMaxCoordinate.getColumn();
                final Coordinate maxCoordinate = new Coordinate(maxRow, maxColumn);
                boundingBox.setMaxCoordinate(maxCoordinate);

                findBoundingBox(boundingBoxGrid, coordinateBelow, boundingBox);
            }
        }
    }

    private static void findBoundingBoxInCoordinateToLeft(final BoundingBoxGrid boundingBoxGrid, final BoundingBox boundingBox, final int currentRow, final int currentColumn) {
        final int columnToLeft = currentColumn - 1;
        final Coordinate coordinateToLeft = new Coordinate(currentRow, columnToLeft);
        if (boundingBoxGrid.hasCoordinate(coordinateToLeft)) {
            final BoundingBox coordinateToLeftBoundingBox = boundingBoxGrid.getBoundingBox(coordinateToLeft);
            if (coordinateToLeftBoundingBox == null) {
                final Coordinate boundingBoxMinCoordinate = boundingBox.getMinCoordinate();
                final int minRow = boundingBoxMinCoordinate.getRow();
                final int boundingBoxMinColumn = boundingBoxMinCoordinate.getColumn();
                final int minColumn = Math.min(columnToLeft, boundingBoxMinColumn);
                final Coordinate minCoordinate = new Coordinate(minRow, minColumn);
                boundingBox.setMinCoordinate(minCoordinate);

                findBoundingBox(boundingBoxGrid, coordinateToLeft, boundingBox);
            }
        }
    }

    private static Set<BoundingBox> getLargestBoundingBoxes(final Set<BoundingBox> boundingBoxes) {
        final Set<BoundingBox> largestBoundingBoxes = new HashSet<>();

        if (boundingBoxes != null && !boundingBoxes.isEmpty()) {
            int maxArea = 0;
            for (final BoundingBox boundingBox : boundingBoxes) {
                final int area = boundingBox.getArea();
                if (area >= maxArea) {
                    if (area > maxArea) {
                        maxArea = area;
                        largestBoundingBoxes.clear();
                    }
                    largestBoundingBoxes.add(boundingBox);
                }
            }
        }

        return largestBoundingBoxes;
    }

    private static void logBoundingBoxes(final Set<BoundingBox> boundingBoxes) {
        if (boundingBoxes == null || boundingBoxes.isEmpty()) {
            LOGGER.info("No bounding boxes found.");
        } else {
            for (final BoundingBox boundingBox : boundingBoxes) {
                final String boundingBoxInfo = getBoundingBoxInfo(boundingBox);
                LOGGER.info(boundingBoxInfo);
            }
        }
    }

    private static String getBoundingBoxInfo(final BoundingBox boundingBox) {
        @SuppressWarnings("DuplicatedCode") final Coordinate minCoordinate = boundingBox.getMinCoordinate();
        final int minRow = minCoordinate.getRow();
        final int minColumn = minCoordinate.getColumn();

        final Coordinate maxCoordinate = boundingBox.getMaxCoordinate();
        final int maxRow = maxCoordinate.getRow();
        final int maxColumn = maxCoordinate.getColumn();

        return String.format(BOUNDING_BOX_FORMAT,
                minRow,
                minColumn,
                maxRow,
                maxColumn);
    }

}
