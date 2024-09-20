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
                // addRookMoves(board, myPosition, moves);
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
            moves.add(new ChessMove(myPosition, forwardPosition, null));
        }

        // INITIAL MOVE - MOVE TWO SQUARES IF NOT BLOCKED
        if ((this.pieceColor == ChessGame.TeamColor.WHITE && row == 1) ||
                (this.pieceColor == ChessGame.TeamColor.BLACK && row == 6)) {
            ChessPosition twoStepsForward = new ChessPosition(row + 2 * direction, col);
            if (isPositionValid(board, twoStepsForward) && board.getPiece(twoStepsForward) == null) {
                moves.add(new ChessMove(myPosition, twoStepsForward, null));
            }
        }

        ChessPosition captureLeft = new ChessPosition(row + direction, col -1);
        ChessPosition captureRight = new ChessPosition(row + direction, col -1);

        if (isPositionValid(board, captureLeft) && board.getPiece(captureLeft) != null && board.getPiece(captureLeft).getTeamColor() != this.pieceColor) {
            moves.add(new ChessMove(myPosition, captureLeft, null));
        }

        if (isPositionValid(board, captureRight) && board.getPiece(captureRight) != null && board.getPiece(captureRight).getTeamColor() != this.pieceColor) {
            moves.add(new ChessMove(myPosition, captureRight, null));
        }
    }

    private boolean isPositionValid(ChessBoard board, ChessPosition pos) {
        return pos.getRow() >= 0 && pos.getRow() < 8 && pos.getColumn() >= 0 && pos.getColumn() < 8;
    }

}