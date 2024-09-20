package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;

    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        if (piece == null) {
            return moves; //No Piece
        }

        switch (piece.getPieceType()) {
            case PAWN:
                addPawnMoves(board, myPosition, moves);
                break;
            case ROOK:
                addRookMoves(board, myPosition, moves);
                break;
            case BISHOP:
                // addBishopMoves(board, myPosition, moves);
                break;
            case KNIGHT:
                // addKnightMoves(board, myPosition, moves);
                break;
            case QUEEN:
                // addQueenMoves(board, myPosition, moves);
                break;
            case KING:
                // addKingMoves(board, myPosition, moves);
                break;
            default:
                throw new IllegalStateException("Error: Unexpected piece type: " + piece);
        }
        return moves;
    }

    //PAWN MOVES
    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int direction = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        // PAWN MOVE FORWARD LOGIC
        ChessPosition forwardPosition = new ChessPosition(row + direction, col);
        if (isPositionValid(board, forwardPosition) && board.getPiece(forwardPosition) == null) {
            // Check for promotion (reaching the last rank)
            if ((this.pieceColor == ChessGame.TeamColor.WHITE && row + direction == 7) ||
                    (this.pieceColor == ChessGame.TeamColor.BLACK && row + direction == 0)) {
                // Add all possible promotion options (Queen, Rook, Bishop, Knight)
                moves.add(new ChessMove(myPosition, forwardPosition, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, forwardPosition, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, forwardPosition, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, forwardPosition, ChessPiece.PieceType.KNIGHT));
            } else {
                // Normal pawn forward move
                moves.add(new ChessMove(myPosition, forwardPosition, null));
            }
        }

        // INITIAL MOVE - MOVE TWO SQUARES IF NOT BLOCKED
        if ((this.pieceColor == ChessGame.TeamColor.WHITE && row == 1) ||
                (this.pieceColor == ChessGame.TeamColor.BLACK && row == 6)) {
            ChessPosition forwardOneStep = new ChessPosition(row + direction, col);
            ChessPosition twoStepsForward = new ChessPosition(row + 2 * direction, col);
            if (isPositionValid(board, forwardOneStep) && isPositionValid(board, twoStepsForward) &&
                    board.getPiece(forwardOneStep) == null && board.getPiece(twoStepsForward) == null) {
                moves.add(new ChessMove(myPosition, twoStepsForward, null));
            }
        }

        // CAPTURE LEFT
        if (col > 0) {  // Prevent out of bounds on the left
            ChessPosition captureLeft = new ChessPosition(row + direction, col - 1);
            if (isPositionValid(board, captureLeft) && board.getPiece(captureLeft) != null &&
                    board.getPiece(captureLeft).getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, captureLeft, null));
            }
        }

        // CAPTURE RIGHT
        if (col < 7) {  // Prevent out of bounds on the right
            ChessPosition captureRight = new ChessPosition(row + direction, col + 1);
            if (isPositionValid(board, captureRight) && board.getPiece(captureRight) != null &&
                    board.getPiece(captureRight).getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, captureRight, null));
            }
        }
    }

    private boolean isPositionValid(ChessBoard board, ChessPosition pos) {
        return pos.getRow() >= 0 && pos.getRow() < 8 && pos.getColumn() >= 0 && pos.getColumn() < 8;
    }

    private void addRookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        addStraightLineMoves(board, myPosition, moves, 1, 0);
        addStraightLineMoves(board, myPosition, moves, -1, 0);
        addStraightLineMoves(board, myPosition, moves, 0, 1);
        addStraightLineMoves(board, myPosition, moves, 0, -1);
    }

    private void addBishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        addDiagonalMoves(board, myPosition, moves, 1, 1);
        addDiagonalMoves(board, myPosition, moves, 1, -1);
        addDiagonalMoves(board, myPosition, moves, -1, 1);
        addDiagonalMoves(board, myPosition, moves, -1, -1);

    }

    private void addKnightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] knightMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] move: knightMoves) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1] );
            if (isPositionValid(board, newPos) && (board.getPiece(newPos) == null || board.getPiece(newPos).getTeamColor() != this.pieceColor)) {
                moves.add(new ChessMove(myPosition, newPos, null));
            }
        }
    }

    private void addQueenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        addRookMoves(board, myPosition, moves);
        addBishopMoves(board, myPosition, moves);
    }

    private void addKingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] kingMoves = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        for (int[] move : kingMoves) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
            if (isPositionValid(board, newPos) && (board.getPiece(newPos) == null || board.getPiece(newPos).getTeamColor() != this.pieceColor)) {
                moves.add(new ChessMove(myPosition, newPos, null));
            }
        }
    }

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
            moves.add(new ChessMove(myPosition, newPos, null));
            if (board.getPiece(newPos) != null) {
                break;
            }
        }
    }

    private void addDiagonalMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int rowStep, int colStep) {
        addStraightLineMoves(board, myPosition, moves, rowStep, colStep);
    }

}