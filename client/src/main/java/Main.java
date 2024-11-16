import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Scanner;
import client.ServerFacade;
import ui.EscapeSequences;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.*;
import com.google.gson.*;
import java.util.*;
import com.google.gson.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static ServerFacade serverFacade = new ServerFacade("http://localhost:8080"); // Replace with actual server URL
    private static String authToken = null;
    private static final Gson gson = new Gson();
    private static final Map<Integer, Integer> displayNumberToGameId = new HashMap<>();
    private static int currentDisplayNumber = 1;
    private static final Map<String, Integer> gameNameToId = new HashMap<>();



    public static void main(String[] args) {
        System.out.println("♕ Welcome to Chess Client! Type 'help' for available commands.");
        preloginUI();
    }

    private static String extractAuthToken(String jsonString) {
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        return jsonObject.get("authToken").getAsString();
    }

    private static void preloginUI() {
        while (authToken == null) {
            System.out.print("[LOGGED_OUT] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayPreloginHelp();
                    break;
                case "register":
                    register();
                    break;
                case "login":
                    login();
                    break;
                case "quit":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
        postloginUI();
    }

    private static void postloginUI() {
        while (authToken != null) {
            System.out.print("[LOGGED_IN] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayPostloginHelp();
                    break;
                case "logout":
                    logout();
                    break;
                case "create":
                    createGame();
                    break;
                case "list":
                    listGames();
                    break;
                case "join":
                    joinGame();
                    break;
                case "observe":
                    displayChessBoard();
                    break;
                case "quit":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
        preloginUI();
    }

    private static void displayPreloginHelp() {
        System.out.println("Available commands:");
        System.out.println("  register <username> <password> <email> - Register a new account");
        System.out.println("  login <username> <password> - Login to your account");
        System.out.println("  quit - Exit the application");
    }

    private static void displayPostloginHelp() {
        System.out.println("Available commands:");
        System.out.println("  create <gameName> - Create a new game");
        System.out.println("  list - List all games");
        System.out.println("  join - Join or observe a game");
        System.out.println("  logout - Logout of your account");
        System.out.println("  quit - Exit the application");
    }

    private static void login() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Username and password cannot be empty.");
                return;
            }

            String response = serverFacade.login(username, password);
            authToken = extractAuthToken(response);
            System.out.println("Welcome back, " + username + "!");
        } catch (IOException e) {
            System.out.println("Invalid username or password.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }


    private static void register() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                System.out.println("All fields are required.");
                return;
            }

            String response = serverFacade.register(username, password, email);
            authToken = extractAuthToken(response);
            System.out.println("Welcome, " + username + "!");
        } catch (IOException e) {
            System.out.println("Unable to register. Username may already be taken.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void logout() {
        try {
            serverFacade.logout(authToken);
            System.out.println("Goodbye!");
            authToken = null;
        } catch (IOException e) {
            System.out.println("Unable to logout. Please try again.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void createGame() {
        try {
            System.out.print("Enter game name: ");
            String gameName = scanner.nextLine().trim();

            if (gameName.isEmpty()) {
                System.out.println("Game name cannot be empty.");
                return;
            }

            serverFacade.createGame(authToken, gameName);
            System.out.println("Game '" + gameName + "' created successfully.");
        } catch (IOException e) {
            System.out.println("Unable to create game. Please try again.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void listGames() {
        try {
            String gamesJson = serverFacade.listGames(authToken);
            JsonObject gamesObject = gson.fromJson(gamesJson, JsonObject.class);
            JsonArray games = gamesObject.getAsJsonArray("games");

            if (games.size() == 0) {
                System.out.println("No games available.");
                return;
            }

            // Clear previous mapping
            gameNameToId.clear();

            System.out.println("\nAvailable Games:");
            System.out.println("----------------");
            for (JsonElement gameElement : games) {
                JsonObject game = gameElement.getAsJsonObject();
                String gameName = game.get("gameName").getAsString();
                String whitePlayer = game.has("whiteUsername") ? game.get("whiteUsername").getAsString() : "<EMPTY>";
                String blackPlayer = game.has("blackUsername") ? game.get("blackUsername").getAsString() : "<EMPTY>";
                int gameId = game.get("gameID").getAsInt();

                // Store the mapping
                gameNameToId.put(gameName, gameId);

                System.out.printf("Game: %s\n", gameName);
                System.out.printf("  White Player: %s\n", whitePlayer);
                System.out.printf("  Black Player: %s\n", blackPlayer);
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Unable to retrieve games. Please try again.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void joinGame() {
        try {
            // First list the games so user can see available games
            listGames();

            System.out.print("Enter game name: ");
            String gameName = scanner.nextLine().trim();
            System.out.print("Enter color (WHITE/BLACK/empty to observe): ");
            String color = scanner.nextLine().trim().toUpperCase();

            // Input validation
            if (gameName.isEmpty()) {
                System.out.println("Game name cannot be empty.");
                return;
            }

            // Get the game ID from our mapping
            Integer gameId = gameNameToId.get(gameName);
            if (gameId == null) {
                System.out.println("Game not found. Please enter an existing game name from the list.");
                return;
            }

            if (!color.isEmpty() && !color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color. Please enter WHITE, BLACK, or leave empty to observe.");
                return;
            }

            serverFacade.joinGame(authToken, gameId, color);

            if (color.isEmpty()) {
                System.out.printf("Observing game '%s'.\n", gameName);
            } else {
                System.out.printf("Joined game '%s' as %s.\n", gameName, color);
            }
            displayChessBoard();

        } catch (IOException e) {
            System.out.println("Unable to join game. Please verify the game name and try again.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void displayChessBoard() {
        String[][] board = {
                {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK},
                {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN},
                {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK}
        };

        System.out.println("White's perspective:");
        printBoard(board, true);

        System.out.println("\nBlack's perspective:");
        printBoard(board, false);
    }

    private static void printBoard(String[][] board, boolean whitePerspective) {
        if (!whitePerspective) {
            for (int i = 0; i < board.length / 2; i++) {
                String[] temp = board[i];
                board[i] = board[board.length - 1 - i];
                board[board.length - 1 - i] = temp;
            }
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if ((i + j) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + board[i][j] + EscapeSequences.RESET_BG_COLOR);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + board[i][j] + EscapeSequences.RESET_BG_COLOR);
                }
            }
            System.out.println();
        }
    }
}
