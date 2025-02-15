package com.the1katman;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BoundingBoxGrid {

    private final Map<Coordinate, BoundingBox> _grid = new HashMap<>();

    boolean hasCoordinate(final Coordinate coordinate) {
        return _grid.containsKey(coordinate);
    }

    Set<Coordinate> getCoordinates() {
        return _grid.keySet();
    }

    void markCoordinate(final Coordinate coordinate) {
        _grid.put(coordinate, null);
    }

    BoundingBox getBoundingBox(final Coordinate coordinate) {
        return _grid.get(coordinate);
    }

    void markBoundingBox(final Coordinate coordinate, final BoundingBox boundingBox) {
        _grid.put(coordinate, boundingBox);
    }

    Set<BoundingBox> markBoundingBox(final BoundingBox boundingBox) {
        final Set<BoundingBox> overlappingBoundingBoxes = new HashSet<>();

        @SuppressWarnings("DuplicatedCode") final Coordinate minCoordinate = boundingBox.getMinCoordinate();
        final int minRow = minCoordinate.getRow();
        final int minColumn = minCoordinate.getColumn();

        final Coordinate maxCoordinate = boundingBox.getMaxCoordinate();
        final int maxRow = maxCoordinate.getRow();
        final int maxColumn = maxCoordinate.getColumn();

        for (int row = minRow; row <= maxRow; row++) {
            for (int column = minColumn; column <= maxColumn; column++) {
                final Coordinate coordinate = new Coordinate(row, column);
                final BoundingBox existingBoundingBox = _grid.get(coordinate);
                if (existingBoundingBox != null && !boundingBox.equals(existingBoundingBox)) {
                    overlappingBoundingBoxes.add(existingBoundingBox);
                    overlappingBoundingBoxes.add(boundingBox);
                }
                markBoundingBox(coordinate, boundingBox);
            }
        }

        return overlappingBoundingBoxes;
    }

}
