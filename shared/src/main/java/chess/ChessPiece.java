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

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();

        if (piece == null) {
            return moves;
        }

        switch (piece.getPieceType()) {
            case PAWN:
                // Implement pawn move logic
                addPawnMoves(board, myPosition, moves);
                break;
            case ROOK:
                // Implement rook move logic
                addRookMoves(board, myPosition, moves);
                break;
            case BISHOP:
                // Implement bishop move logic
                addBishopMoves(board, myPosition, moves);
                break;
            case KNIGHT:
                // Implement knight move logic
                addKnightMoves(board, myPosition, moves);
                break;
            case QUEEN:
                // Implement queen move logic
                addQueenMoves(board, myPosition, moves);
                break;
            case KING:
                // Implement king move logic
                addKingMoves(board, myPosition, moves);
                break;
            default:
                throw new IllegalStateException("Unexpected piece type: " + piece.getType());
        }
        // Example methods for adding specific piece moves

        private void addPawnMoves (ChessBoard board, ChessPosition position, List < ChessMove > moves){
            // Implement pawn movement logic here
        }

        private void addRookMoves (ChessBoard board, ChessPosition position, List < ChessMove > moves){
            // Implement rook movement logic here
        }

        private void addBishopMoves (ChessBoard board, ChessPosition position, List < ChessMove > moves){
            // Implement bishop movement logic here
        }

        private void addKnightMoves (ChessBoard board, ChessPosition position, List < ChessMove > moves){
            // Implement knight movement logic here
        }

        private void addQueenMoves (ChessBoard board, ChessPosition position, List < ChessMove > moves){
            // Implement queen movement logic here
        }

        private void addKingMoves (ChessBoard board, ChessPosition position, List < ChessMove > moves){
            // Implement king movement logic here
        }
    }
}
