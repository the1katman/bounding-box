package com.the1katman;

import java.util.Objects;

public class Coordinate {

    private final int _row;
    private final int _column;

    public Coordinate(final int row, final int column) {
        _row = row;
        _column = column;
    }

    int getRow() {
        return _row;
    }

    int getColumn() {
        return _column;
    }

    @Override
    public boolean equals(final Object obj) {
        boolean equals = obj == this;

        if (!equals && obj instanceof final Coordinate other) {
            equals = Objects.equals(_row, other._row) && Objects.equals(_column, other._column);
        }

        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_row, _column);
    }

}
