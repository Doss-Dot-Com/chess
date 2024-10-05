package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manages the entire chess game, including moves, check, checkmate, and stalemate.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE; // Start with WHITE's turn
    }

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null || piece.getTeamColor() != teamTurn) {
            return null; // No piece or wrong team's turn
        }

        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : potentialMoves) {
            // Create a temporary board to simulate the move
            ChessBoard tempBoard = new ChessBoard();
            copyBoardState(board, tempBoard);

            // Simulate the move
            tempBoard.addPiece(move.getEndPosition(), piece);
            tempBoard.addPiece(move.getStartPosition(), null);

            // Verify if the king is in check
            if (!isInCheck(piece.getTeamColor(), tempBoard)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece piece = board.getPiece(start);
        if (piece == null || piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Error: Invalid Move");
        }

        Collection<ChessMove> validMoves = validMoves(start);
        boolean isValid = false;

        if (validMoves != null) {
            for (ChessMove validMove : validMoves) {
                if (validMove.getEndPosition().equals(end)) {
                    isValid = true;
                    break;
                }
            }
        }

        if (!isValid) {
            throw new InvalidMoveException("Error: Not Legal");
        }

        // Perform the move
        board.addPiece(end, piece);
        board.addPiece(start, null);

        // Switch the turn to the other team
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, board);
    }

    public boolean isInCheck(TeamColor teamColor, ChessBoard boardToCheck) {
        ChessPosition kingPosition = findKingPosition(teamColor, boardToCheck);
        if (kingPosition == null) {
            return false; // Should not happen unless the king is missing from the board
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = boardToCheck.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> opponentMoves = piece.pieceMoves(boardToCheck, position);
                    for (ChessMove move : opponentMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true; // The king is in check
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);
                    if (moves != null && !moves.isEmpty()) {
                        return false; // There is at least one move that can escape the check
                    }
                }
            }
        }
        return true;
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);
                    if (moves != null && !moves.isEmpty()) {
                        return false; // There is at least one legal move
                    }
                }
            }
        }
        return true;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return board;
    }

    private void copyBoardState(ChessBoard sourceBoard, ChessBoard targetBoard) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = sourceBoard.getPiece(position);
                if (piece != null) {
                    targetBoard.addPiece(position, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
    }

    private ChessPosition findKingPosition(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }

    public enum TeamColor {
        WHITE, BLACK
    }
}

