package chess;

/* Quality Enhancement */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Manages the entire chess game, including moves, check, checkmate, and stalemate.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    // Constructor: Initializes the board and starts with WHITE's turn
    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    // Getter for the current team's turn
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    // Setter for the current team's turn
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Retrieves all valid moves for a piece at a given position.
     *
     * @param startPosition The position of the piece to move
     * @return Collection of valid moves
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        // Return an empty collection if no piece exists at the position or if it's not the team's turn
        if (piece == null) {
            return new ArrayList<>();  // Return an empty list instead of null
        }

        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Verify each potential move to ensure it doesn't leave the king in check
        for (ChessMove move : potentialMoves) {
            if (isMoveSafe(move, piece)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    /**
     * Checks if a move would leave the king in check on a temporary board.
     *
     * @param move The move to simulate
     * @param piece The piece to move
     * @return true if the move is safe, false if it leaves the king in check
     */
    private boolean isMoveSafe(ChessMove move, ChessPiece piece) {
        ChessBoard tempBoard = new ChessBoard();
        copyBoardState(board, tempBoard);

        // Simulate the move on a temporary board
        tempBoard.addPiece(move.getEndPosition(), piece);
        tempBoard.addPiece(move.getStartPosition(), null);

        // Check if the king would be in check after this move
        return !isInCheck(piece.getTeamColor(), tempBoard);
    }

    /**
     * Executes a move on the board.
     *
     * @param move The move to be made
     * @throws InvalidMoveException if the move is not valid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece piece = board.getPiece(start);
        if (piece == null || piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Error: Invalid Move");
        }

        // Validate that the move is legal
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

        // Handle pawn promotion
        if (move.getPromotionPiece() != null) {
            ChessPiece promotionPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(end, promotionPiece);
        } else {
            board.addPiece(end, piece);
        }

        // Perform the move by clearing the starting position
        board.addPiece(start, null);

        // Switch the turn to the other team
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Checks if the given team is in check.
     *
     * @param teamColor The team to check
     * @return true if the team is in check, false otherwise
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, board);
    }

    /**
     * Checks if the given team is in check on a specific board state.
     *
     * @param teamColor The team to check
     * @param boardToCheck The board state to check
     * @return true if the team is in check, false otherwise
     */
    public boolean isInCheck(TeamColor teamColor, ChessBoard boardToCheck) {
        ChessPosition kingPosition = findKingPosition(teamColor, boardToCheck);
        if (kingPosition == null) {
            return false; // Should not happen unless the king is missing from the board
        }

        // Iterate through each square to find if any opponent piece can capture the king
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

    /**
     * Checks if the given team is in checkmate.
     *
     * @param teamColor The team to check
     * @return true if the team is in checkmate, false otherwise
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        // Check if the team has any legal moves to get out of check
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

    /**
     * Checks if the given team is in stalemate.
     *
     * @param teamColor The team to check
     * @return true if the team is in stalemate, false otherwise
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        // Check if the team has any legal moves available
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

    // Setter for the board
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    // Getter for the board
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Copies the state of one board to another.
     *
     * @param sourceBoard The board to copy from
     * @param targetBoard The board to copy to
     */
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

    /**
     * Finds the position of the king for a given team.
     *
     * @param teamColor The team to find the king for
     * @param board The board to search
     * @return The position of the king or null if not found
     */
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

    // Enumeration to represent team colors
    public enum TeamColor {
        WHITE, BLACK
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", teamTurn=" + teamTurn +
                '}';
    }
}


