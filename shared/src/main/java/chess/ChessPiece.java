package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece.
 * <p>
 * Note: You can add to this class, but you may not alter
 * the signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    // Constructor: Initializes the piece's color and type
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The different types of chess pieces.
     */
    public enum PieceType {
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
    }

    /**
     * @return Which team this chess piece belongs to.
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return The type of chess piece (e.g., PAWN, KING).
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to.
     * Does not take into account moves that are illegal due to leaving the king in danger.
     *
     * @param board      The current state of the chessboard.
     * @param myPosition The current position of the piece.
     * @return Collection of valid moves.
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        if (piece == null) {
            return moves; // No Piece present
        }

        switch (piece.getPieceType()) {
            case PAWN -> addPawnMoves(board, myPosition, moves);
            case ROOK -> addRookMoves(board, myPosition, moves);
            case BISHOP -> addBishopMoves(board, myPosition, moves);
            case KNIGHT -> addKnightMoves(board, myPosition, moves);
            case QUEEN -> addQueenMoves(board, myPosition, moves);
            case KING -> addKingMoves(board, myPosition, moves);
            default -> throw new IllegalStateException("Error: Unexpected piece type: " + piece);
        }
        return moves;
    }

    // Adds valid moves for a pawn from a given position
    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int direction = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        // PAWN MOVE FORWARD LOGIC
        ChessPosition forwardPosition = new ChessPosition(row + direction, col);
        if (isPositionValid(board, forwardPosition) && board.getPiece(forwardPosition) == null) {
            if (isPromotion(row + direction)) {
                addPromotionMoves(myPosition, forwardPosition, moves);
            } else {
                moves.add(new ChessMove(myPosition, forwardPosition, null)); // Normal pawn forward move
            }
        }

        // INITIAL MOVE - MOVE TWO SQUARES IF NOT BLOCKED
        if (isPawnInitialPosition(row)) {
            ChessPosition forwardOneStep = new ChessPosition(row + direction, col);
            ChessPosition twoStepsForward = new ChessPosition(row + 2 * direction, col);
            if (isPositionValid(board, forwardOneStep) && isPositionValid(board, twoStepsForward) &&
                    board.getPiece(forwardOneStep) == null && board.getPiece(twoStepsForward) == null) {
                moves.add(new ChessMove(myPosition, twoStepsForward, null));
            }
        }

        // CAPTURE LEFT
        if (col > 1) {
            ChessPosition captureLeft = new ChessPosition(row + direction, col - 1);
            if (isPositionValid(board, captureLeft) && board.getPiece(captureLeft) != null &&
                    board.getPiece(captureLeft).getTeamColor() != this.pieceColor) {
                if (isPromotion(row + direction)) {
                    addPromotionMoves(myPosition, captureLeft, moves);
                } else {
                    moves.add(new ChessMove(myPosition, captureLeft, null));
                }
            }
        }

        // CAPTURE RIGHT
        if (col < 8) {
            ChessPosition captureRight = new ChessPosition(row + direction, col + 1);
            if (isPositionValid(board, captureRight) && board.getPiece(captureRight) != null &&
                    board.getPiece(captureRight).getTeamColor() != this.pieceColor) {
                if (isPromotion(row + direction)) {
                    addPromotionMoves(myPosition, captureRight, moves);
                } else {
                    moves.add(new ChessMove(myPosition, captureRight, null));
                }
            }
        }
    }

    // Checks if a pawn is eligible for promotion (reaching the last rank)
    private boolean isPromotion(int row) {
        return (this.pieceColor == ChessGame.TeamColor.WHITE && row == 8) ||
                (this.pieceColor == ChessGame.TeamColor.BLACK && row == 1);
    }

    // Adds all possible promotion options for a pawn
    private void addPromotionMoves(ChessPosition start, ChessPosition end, Collection<ChessMove> moves) {
        moves.add(new ChessMove(start, end, PieceType.QUEEN));
        moves.add(new ChessMove(start, end, PieceType.ROOK));
        moves.add(new ChessMove(start, end, PieceType.BISHOP));
        moves.add(new ChessMove(start, end, PieceType.KNIGHT));
    }

    // Checks if the pawn is in its initial position (eligible to move two steps forward)
    private boolean isPawnInitialPosition(int row) {
        return (this.pieceColor == ChessGame.TeamColor.WHITE && row == 2) ||
                (this.pieceColor == ChessGame.TeamColor.BLACK && row == 7);
    }

    // Adds valid moves for a rook
    private void addRookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        addStraightLineMoves(board, myPosition, moves, 1, 0);
        addStraightLineMoves(board, myPosition, moves, -1, 0);
        addStraightLineMoves(board, myPosition, moves, 0, 1);
        addStraightLineMoves(board, myPosition, moves, 0, -1);
    }

    // Adds valid moves for a bishop
    private void addBishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        addDiagonalMoves(board, myPosition, moves, 1, 1);
        addDiagonalMoves(board, myPosition, moves, 1, -1);
        addDiagonalMoves(board, myPosition, moves, -1, 1);
        addDiagonalMoves(board, myPosition, moves, -1, -1);
    }

    // Adds valid moves for a knight
    private void addKnightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] knightMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] move : knightMoves) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
            if (isPositionValid(board, newPos) &&
                    (board.getPiece(newPos) == null || board.getPiece(newPos).getTeamColor() != this.pieceColor)) {
                moves.add(new ChessMove(myPosition, newPos, null));
            }
        }
    }

    // Adds valid moves for a queen (combines rook and bishop moves)
    private void addQueenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        addRookMoves(board, myPosition, moves);
        addBishopMoves(board, myPosition, moves);
    }

    // Adds valid moves for a king
    private void addKingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] kingMoves = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        for (int[] move : kingMoves) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
            if (isPositionValid(board, newPos) &&
                    (board.getPiece(newPos) == null || board.getPiece(newPos).getTeamColor() != this.pieceColor)) {
                moves.add(new ChessMove(myPosition, newPos, null));
            }
        }
    }

    // Adds valid straight-line moves for rook, bishop, or queen
    private void addStraightLineMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int rowStep, int colStep) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (true) {
            row += rowStep;
            col += colStep;
            ChessPosition newPos = new ChessPosition(row, col);
            if (!isPositionValid(board, newPos)) {
                break;
            }
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, newPos, null)); // Capture opponent piece
                }
                break; // Block further movement
            }
            moves.add(new ChessMove(myPosition, newPos, null)); // Normal move
        }
    }

    // Adds valid diagonal moves for a bishop or queen
    private void addDiagonalMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int rowStep, int colStep) {
        addStraightLineMoves(board, myPosition, moves, rowStep, colStep);
    }

    // Checks if the position is valid within the chessboard boundaries
    private boolean isPositionValid(ChessBoard board, ChessPosition pos) {
        return pos.getRow() >= 1 && pos.getRow() <= 8 && pos.getColumn() >= 1 && pos.getColumn() <= 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                "}\n";
    }
}
