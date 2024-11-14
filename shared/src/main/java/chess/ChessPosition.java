package chess;

import java.util.Objects;

/**
 * Represents a specific position on the chess board.
 */
public class ChessPosition {

    // Fields representing the row and column of the chess position
    private final int row;
    private final int column;

    /**
     * Constructor to initialize the row and column of the chess position.
     *
     * @param row The row of the position (1-8 for a standard chess board)
     * @param column The column of the position (1-8 for a standard chess board)
     */
    public ChessPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    // Getter for the row value
    public int getRow() {
        return row;
    }

    // Getter for the column value
    public int getColumn() {
        return column;
    }

    /**
     * Determines if two ChessPosition objects are equal.
     *
     * @param o The other object to compare
     * @return true if both objects represent the same position, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;  // Check if both references are the same
        }
        if (o == null || getClass() != o.getClass()) {
            return false; // Ensure the object is not null and is of the same class
        }

        ChessPosition that = (ChessPosition) o;
        return row == that.row && column == that.column;  // Compare the row and column values
    }

    /**
     * Generates a hash code for the ChessPosition object.
     *
     * @return The hash code based on the row and column values
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}

