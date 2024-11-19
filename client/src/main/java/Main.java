import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Scanner;
import client.ServerFacade;
import ui.ChessBoard;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.*;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    private static String authToken = null;
    private static final Gson GSON = new Gson();
    private static final Map<Integer, Integer> DISPLAY_TO_GAME_ID = new HashMap<>();
    private static final Map<Integer, String> DISPLAY_TO_GAME_NAME = new HashMap<>();



    public static void main(String[] args) {
        System.out.println("♕ Welcome to Chess Client! Type 'help' for available commands.");
        preloginUI();
    }

    private static String extractAuthToken(String jsonString) {
        JsonObject jsonObject = GSON.fromJson(jsonString, JsonObject.class);
        return jsonObject.get("authToken").getAsString();
    }

    private static void preloginUI() {
        while (authToken == null) {
            System.out.print("[LOGGED_OUT] >>> ");
            String command = SCANNER.nextLine().trim().toLowerCase();

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
            String command = SCANNER.nextLine().trim().toLowerCase();

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
                    observe();
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
        System.out.println("  join - Join a game as a player");
        System.out.println("  observe - Observe an existing game");
        System.out.println("  logout - Logout of your account");
        System.out.println("  quit - Exit the application");
    }

    private static void login() {
        try {
            System.out.print("Enter username: ");
            String username = SCANNER.nextLine().trim();
            System.out.print("Enter password: ");
            String password = SCANNER.nextLine().trim();

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
            String username = SCANNER.nextLine().trim();
            System.out.print("Enter password: ");
            String password = SCANNER.nextLine().trim();
            System.out.print("Enter email: ");
            String email = SCANNER.nextLine().trim();

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
            String gameName = SCANNER.nextLine().trim();

            if (gameName.isEmpty()) {
                System.out.println("Game name cannot be empty.");
                return;
            }

            // Check existing games for duplicate names
            String gamesJson = serverFacade.listGames(authToken);
            JsonObject gamesObject = GSON.fromJson(gamesJson, JsonObject.class);
            JsonArray games = gamesObject.getAsJsonArray("games");

            // Check for duplicate game names
            for (JsonElement gameElement : games) {
                JsonObject game = gameElement.getAsJsonObject();
                if (game.get("gameName").getAsString().equals(gameName)) {
                    System.out.println("A game with this name already exists. Please choose a different name.");
                    return;
                }
            }

            // If no duplicates found, create the game
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
            JsonObject gamesObject = GSON.fromJson(gamesJson, JsonObject.class);
            JsonArray games = gamesObject.getAsJsonArray("games");

            if (games.size() == 0) {
                System.out.println("No games available.");
                return;
            }

            // Clear previous mappings
            DISPLAY_TO_GAME_ID.clear();
            DISPLAY_TO_GAME_NAME.clear();

            System.out.println("\nAvailable Games:");
            System.out.println("----------------");
            int displayNumber = 1;
            for (JsonElement gameElement : games) {
                JsonObject game = gameElement.getAsJsonObject();
                String gameName = game.get("gameName").getAsString();
                String whitePlayer = game.has("whiteUsername") ? game.get("whiteUsername").getAsString() : "<EMPTY>";
                String blackPlayer = game.has("blackUsername") ? game.get("blackUsername").getAsString() : "<EMPTY>";
                int gameId = game.get("gameID").getAsInt();

                // Store the mappings
                DISPLAY_TO_GAME_ID.put(displayNumber, gameId);
                DISPLAY_TO_GAME_NAME.put(displayNumber, gameName);

                System.out.printf("%d. Game: %s\n", displayNumber, gameName);
                System.out.printf("   White Player: %s\n", whitePlayer);
                System.out.printf("   Black Player: %s\n", blackPlayer);
                System.out.println();

                displayNumber++;
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

            if (DISPLAY_TO_GAME_ID.isEmpty()) {
                return; // No games available
            }

            System.out.print("Enter game number: ");
            int gameNumber;
            try {
                gameNumber = Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid game number.");
                return;
            }

            if (!DISPLAY_TO_GAME_ID.containsKey(gameNumber)) {
                System.out.println("Invalid game number. Please choose from the list.");
                return;
            }

            System.out.print("Enter color (WHITE/BLACK/empty to observe): ");
            String color = SCANNER.nextLine().trim().toUpperCase();

            if (!color.isEmpty() && !color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color. Please enter WHITE, BLACK, or leave empty to observe.");
                return;
            }

            int actualGameId = DISPLAY_TO_GAME_ID.get(gameNumber);
            String gameName = DISPLAY_TO_GAME_NAME.get(gameNumber);

            serverFacade.joinGame(authToken, actualGameId, color);

            if (color.isEmpty()) {
                System.out.printf("Observing game '%s' (Game #%d).\n", gameName, gameNumber);
                displayChessBoard(null);
            } else {
                System.out.printf("Joined game '%s' (Game #%d) as %s.\n", gameName, gameNumber, color);
                displayChessBoard(color);
            }

        } catch (IOException e) {
            System.out.println("Unable to join game. Please try again.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void observe() {
        try {
            // First list the games so user can see available games
            listGames();

            if (DISPLAY_TO_GAME_ID.isEmpty()) {
                return; // No games available
            }

            System.out.print("Enter game number to observe: ");
            int gameNumber;
            try {
                gameNumber = Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid game number.");
                return;
            }

            if (!DISPLAY_TO_GAME_ID.containsKey(gameNumber)) {
                System.out.println("Invalid game number. Please choose from the list.");
                return;
            }

            String gameName = DISPLAY_TO_GAME_NAME.get(gameNumber);
            System.out.printf("Observing game '%s' (Game #%d).\n", gameName, gameNumber);

            // Just display the white perspective for now
            ChessBoard board = new ChessBoard();
            board.displayWhitePerspective();

        } catch (Exception e) {
            System.out.println("Error displaying chess board.");
        }
    }

    private static void displayChessBoard(String playerColor) {
        try {
            Scanner userInput = new Scanner(System.in);
            ChessBoard board = new ChessBoard();

            // If player is playing (not observing)
            if (playerColor != null) {
                System.out.println("\nPress Enter to see the board from your perspective (" + playerColor + ")...");
                userInput.nextLine();

                // Show player's perspective first
                if (playerColor.equals("BLACK")) {
                    board.displayBlackPerspective();
                } else {
                    board.displayWhitePerspective();
                }

                // Then show the opposite perspective
                System.out.println("\nPress Enter to see the opposite perspective...");
                userInput.nextLine();

                if (playerColor.equals("BLACK")) {
                    board.displayWhitePerspective();
                } else {
                    board.displayBlackPerspective();
                }
            }
            // If player is observing
            else {
                System.out.println("\nPress Enter to see Black's perspective...");
                userInput.nextLine();
                board.displayBlackPerspective();

                System.out.println("\nPress Enter to see White's perspective...");
                userInput.nextLine();
                board.displayWhitePerspective();
            }

        } catch (Exception e) {
            System.out.println("Error displaying chess board. Press Enter to continue.");
        }
    }

    private static void displayChessBoard() {
        try {
            Scanner userInput = new Scanner(System.in);
            ChessBoard board = new ChessBoard();

            System.out.println("\nPress Enter to see the board...");
            userInput.nextLine();

            // Show Black's perspective first
            board.displayBlackPerspective();

            System.out.println("\nPress Enter to see White's perspective...");
            userInput.nextLine();

            // Show White's perspective
            board.displayWhitePerspective();

        } catch (Exception e) {
            System.out.println("Error displaying chess board. Press Enter to continue.");
        }
    }
}
