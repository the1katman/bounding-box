package com.the1katman;

import java.util.Objects;

import static com.the1katman.BoundingBoxApp.BOUNDING_BOX_FORMAT;

public class BoundingBox {

    private Coordinate _minCoordinate;
    private Coordinate _maxCoordinate;

    public BoundingBox(final Coordinate minCoordinate, final Coordinate maxCoordinate) {
        _minCoordinate = minCoordinate;
        _maxCoordinate = maxCoordinate;
    }

    public Coordinate getMinCoordinate() {
        return _minCoordinate;
    }

    public void setMinCoordinate(final Coordinate coordinate) {
        _minCoordinate = coordinate;
    }

    public Coordinate getMaxCoordinate() {
        return _maxCoordinate;
    }

    public void setMaxCoordinate(final Coordinate coordinate) {
        _maxCoordinate = coordinate;
    }

    public Integer getArea() {
        Integer area = null;

        if (_minCoordinate != null && _maxCoordinate != null) {
            final int maxRow = _maxCoordinate.getRow();
            final int minRow = _minCoordinate.getRow();
            final int width = maxRow - minRow + 1;

            final int maxColumn = _maxCoordinate.getColumn();
            final int minColumn = _minCoordinate.getColumn();
            final int height = maxColumn - minColumn + 1;

            area = width * height;
        }

        return area;
    }

    @Override
    public boolean equals(final Object obj) {
        boolean equals = obj == this;

        if (!equals && obj instanceof final BoundingBox other) {
            equals = Objects.equals(_minCoordinate, other._minCoordinate) && Objects.equals(_maxCoordinate, other._maxCoordinate);
        }

        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_minCoordinate, _maxCoordinate);
    }

    @Override
    public String toString() {
        final int minRow = _minCoordinate.getRow();
        final int minColumn = _minCoordinate.getColumn();
        final int maxRow = _maxCoordinate.getRow();
        final int maxColumn = _maxCoordinate.getColumn();
        return String.format(BOUNDING_BOX_FORMAT,
                minRow,
                minColumn,
                maxRow,
                maxColumn);
    }

}
