package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public enum PieceType {
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    public PieceType getPieceType() {
        return this.type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        if (piece == null) {
            return moves;
        }

        switch (piece.getPieceType()) {
            case PAWN -> addPawnMoves(board, myPosition, moves);
            case ROOK -> addStraightLineMoves(board, myPosition, moves, true);
            case BISHOP -> addDiagonalMoves(board, myPosition, moves);
            case KNIGHT -> addKnightMoves(board, myPosition, moves);
            case QUEEN -> {
                addStraightLineMoves(board, myPosition, moves, true);
                addDiagonalMoves(board, myPosition, moves);
            }
            case KING -> addKingMoves(board, myPosition, moves);
            default -> throw new IllegalStateException("Error: Unexpected piece type: " + piece);
        }
        return moves;
    }

    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int direction = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        ChessPosition forwardPosition = new ChessPosition(row + direction, col);
        if (isPositionValid(board, forwardPosition) && board.getPiece(forwardPosition) == null) {
            if (isPromotion(row + direction)) {
                addPromotionMoves(myPosition, forwardPosition, moves);
            } else {
                moves.add(new ChessMove(myPosition, forwardPosition, null));
            }
        }

        if (isPawnInitialPosition(row)) {
            addInitialPawnMove(board, myPosition, row, col, direction, moves);
        }

        addPawnCapture(board, myPosition, row, col, direction, -1, moves); // Capture Left
        addPawnCapture(board, myPosition, row, col, direction, 1, moves);  // Capture Right
    }

    private void addInitialPawnMove(ChessBoard board, ChessPosition myPosition, int row, int col, int direction, Collection<ChessMove> moves) {
        ChessPosition forwardOneStep = new ChessPosition(row + direction, col);
        ChessPosition twoStepsForward = new ChessPosition(row + 2 * direction, col);
        if (isPositionValid(board, forwardOneStep) && isPositionValid(board, twoStepsForward) &&
                board.getPiece(forwardOneStep) == null && board.getPiece(twoStepsForward) == null) {
            moves.add(new ChessMove(myPosition, twoStepsForward, null));
        }
    }

    private void addPawnCapture(ChessBoard board, ChessPosition myPosition, int row, int col, int direction, int colStep, Collection<ChessMove> moves) {
        int newCol = col + colStep;
        if (newCol >= 1 && newCol <= 8) {
            ChessPosition capturePos = new ChessPosition(row + direction, newCol);
            ChessPiece targetPiece = board.getPiece(capturePos);
            if (isPositionValid(board, capturePos) && targetPiece != null && targetPiece.getTeamColor() != this.pieceColor) {
                if (isPromotion(row + direction)) {
                    addPromotionMoves(myPosition, capturePos, moves);
                } else {
                    moves.add(new ChessMove(myPosition, capturePos, null));
                }
            }
        }
    }

    private boolean isPromotion(int row) {
        return (this.pieceColor == ChessGame.TeamColor.WHITE && row == 8) ||
                (this.pieceColor == ChessGame.TeamColor.BLACK && row == 1);
    }

    private void addPromotionMoves(ChessPosition start, ChessPosition end, Collection<ChessMove> moves) {
        moves.add(new ChessMove(start, end, PieceType.QUEEN));
        moves.add(new ChessMove(start, end, PieceType.ROOK));
        moves.add(new ChessMove(start, end, PieceType.BISHOP));
        moves.add(new ChessMove(start, end, PieceType.KNIGHT));
    }

    private boolean isPawnInitialPosition(int row) {
        return (this.pieceColor == ChessGame.TeamColor.WHITE && row == 2) ||
                (this.pieceColor == ChessGame.TeamColor.BLACK && row == 7);
    }

    private void addStraightLineMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, boolean includeOrthogonal) {
        if (includeOrthogonal) {
            addDirectionalMoves(board, myPosition, moves, 1, 0);
            addDirectionalMoves(board, myPosition, moves, -1, 0);
            addDirectionalMoves(board, myPosition, moves, 0, 1);
            addDirectionalMoves(board, myPosition, moves, 0, -1);
        }
    }

    private void addDiagonalMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        addDirectionalMoves(board, myPosition, moves, 1, 1);
        addDirectionalMoves(board, myPosition, moves, 1, -1);
        addDirectionalMoves(board, myPosition, moves, -1, 1);
        addDirectionalMoves(board, myPosition, moves, -1, -1);
    }

    private void addDirectionalMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int rowStep, int colStep) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (true) {
            row += rowStep;
            col += colStep;
            ChessPosition newPos = new ChessPosition(row, col);
            if (!isPositionValid(board, newPos)) {
                break;
            }
            ChessPiece pieceAtNewPos = board.getPiece(newPos);
            if (pieceAtNewPos != null) {
                if (pieceAtNewPos.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
                break;
            }
            moves.add(new ChessMove(myPosition, newPos, null));
        }
    }

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

