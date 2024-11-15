package ui;

/**
 * Represents a chess board that can be displayed in the console
 */
public class ChessBoard {
    private final String[][] board;
    private static final String EMPTY_SQUARE = "           "; // 11 spaces

    public ChessBoard() {
        board = new String[][]{
                {"    " + EscapeSequences.BLACK_ROOK + "    ", "    " + EscapeSequences.BLACK_KNIGHT + "    ", "    " + EscapeSequences.BLACK_BISHOP + "    ", "    " + EscapeSequences.BLACK_QUEEN + "    ", "    " + EscapeSequences.BLACK_KING + "    ", "    " + EscapeSequences.BLACK_BISHOP + "    ", "    " + EscapeSequences.BLACK_KNIGHT + "    ", "    " + EscapeSequences.BLACK_ROOK + "    "},
                {"    " + EscapeSequences.BLACK_PAWN + "    ", "    " + EscapeSequences.BLACK_PAWN + "    ", "    " + EscapeSequences.BLACK_PAWN + "    ", "    " + EscapeSequences.BLACK_PAWN + "    ", "    " + EscapeSequences.BLACK_PAWN + "    ", "    " + EscapeSequences.BLACK_PAWN + "    ", "    " + EscapeSequences.BLACK_PAWN + "    ", "    " + EscapeSequences.BLACK_PAWN + "    "},
                {EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE},
                {EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE},
                {EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE},
                {EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE, EMPTY_SQUARE},
                {"    " + EscapeSequences.WHITE_PAWN + "    ", "    " + EscapeSequences.WHITE_PAWN + "    ", "    " + EscapeSequences.WHITE_PAWN + "    ", "    " + EscapeSequences.WHITE_PAWN + "    ", "    " + EscapeSequences.WHITE_PAWN + "    ", "    " + EscapeSequences.WHITE_PAWN + "    ", "    " + EscapeSequences.WHITE_PAWN + "    ", "    " + EscapeSequences.WHITE_PAWN + "    "},
                {"    " + EscapeSequences.WHITE_ROOK + "    ", "    " + EscapeSequences.WHITE_KNIGHT + "    ", "    " + EscapeSequences.WHITE_BISHOP + "    ", "    " + EscapeSequences.WHITE_QUEEN + "    ", "    " + EscapeSequences.WHITE_KING + "    ", "    " + EscapeSequences.WHITE_BISHOP + "    ", "    " + EscapeSequences.WHITE_KNIGHT + "    ", "    " + EscapeSequences.WHITE_ROOK + "    "}
        };
    }

    public void display() {
        System.out.println();
        printHeader("Black's Perspective");
        displayPerspective(false);

        System.out.println("\n");
        printHeader("White's Perspective");
        displayPerspective(true);
        System.out.println();
    }

    private void printHeader(String text) {
        String headerBorder = "═".repeat(95); // Adjusted width
        System.out.println(EscapeSequences.SET_TEXT_BOLD + "╔" + headerBorder + "╗" + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        System.out.println(EscapeSequences.SET_TEXT_BOLD + "║" + centerText(text, 95) + "║" + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        System.out.println(EscapeSequences.SET_TEXT_BOLD + "╚" + headerBorder + "╝" + EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }

    private void displayPerspective(boolean whitePerspective) {
        printHeaders(whitePerspective);
        printTopBorder();
        printBoard(whitePerspective);
        printBottomBorder();
        printHeaders(whitePerspective);
    }

    private void printHeaders(boolean whitePerspective) {
        System.out.print("      ");
        char[] columns = whitePerspective ? "abcdefgh".toCharArray() : "hgfedcba".toCharArray();
        for (char col : columns) {
            System.out.print(EscapeSequences.SET_TEXT_BOLD + col + "          " + EscapeSequences.RESET_TEXT_BOLD_FAINT); // 10 spaces
        }
        System.out.println();
    }

    private void printTopBorder() {
        System.out.print("    ");
        System.out.println(EscapeSequences.SET_TEXT_BOLD + "╔" + "═".repeat(91) + "╗" + EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    private void printBottomBorder() {
        System.out.print("    ");
        System.out.println(EscapeSequences.SET_TEXT_BOLD + "╚" + "═".repeat(91) + "╝" + EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    private void printBoard(boolean whitePerspective) {
        String[][] displayBoard = copyBoard();
        if (!whitePerspective) {
            reverseBoard(displayBoard);
        }

        int rowNum = whitePerspective ? 8 : 1;
        for (int i = 0; i < displayBoard.length; i++) {
            // Print empty row above
            printEmptyRow(rowNum, i);
            // Print piece row
            printRow(displayBoard[i], rowNum, i);
            // Print empty row below
            printEmptyRow(rowNum, i);

            rowNum += whitePerspective ? -1 : 1;
        }
    }

    private void printEmptyRow(int rowNum, int rowIndex) {
        System.out.print(EscapeSequences.SET_TEXT_BOLD + "    ║" + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        for (int j = 0; j < 8; j++) {
            System.out.print((rowIndex + j) % 2 == 0 ?
                    EscapeSequences.SET_BG_COLOR_WHITE :
                    EscapeSequences.SET_BG_COLOR_DARK_GREEN);
            System.out.print(EMPTY_SQUARE);
            System.out.print(EscapeSequences.RESET_BG_COLOR);
        }
        System.out.println(EscapeSequences.SET_TEXT_BOLD + "║" + EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    private void printRow(String[] row, int rowNum, int rowIndex) {
        System.out.print(EscapeSequences.SET_TEXT_BOLD + String.format(" %d  ║", rowNum) + EscapeSequences.RESET_TEXT_BOLD_FAINT);

        for (int j = 0; j < row.length; j++) {
            printSquare(row[j], (rowIndex + j) % 2 == 0);
        }

        System.out.println(EscapeSequences.SET_TEXT_BOLD + "║  " + rowNum + EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    private void printSquare(String piece, boolean isLightSquare) {
        System.out.print(isLightSquare ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_DARK_GREEN);

        if (piece.trim().isEmpty()) {
            System.out.print(EMPTY_SQUARE);
        } else {
            boolean isWhitePiece = piece.contains("♔") || piece.contains("♕") ||
                    piece.contains("♗") || piece.contains("♘") ||
                    piece.contains("♖") || piece.contains("♙");
            System.out.print(isWhitePiece ? EscapeSequences.SET_TEXT_COLOR_RED : EscapeSequences.SET_TEXT_COLOR_BLUE);
            System.out.print(piece);
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        }

        System.out.print(EscapeSequences.RESET_BG_COLOR);
    }

    private String[][] copyBoard() {
        String[][] copy = new String[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        }
        return copy;
    }

    private void reverseBoard(String[][] board) {
        // Reverse rows
        for (int i = 0; i < board.length / 2; i++) {
            String[] temp = board[i];
            board[i] = board[board.length - 1 - i];
            board[board.length - 1 - i] = temp;
        }
        // Reverse columns
        for (String[] row : board) {
            for (int j = 0; j < row.length / 2; j++) {
                String temp = row[j];
                row[j] = row[row.length - 1 - j];
                row[row.length - 1 - j] = temp;
            }
        }
    }
}