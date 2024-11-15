import java.io.IOException;
import java.util.Scanner;
import client.ServerFacade;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static ServerFacade serverFacade = new ServerFacade("http://localhost:8080"); // Replace with actual server URL
    private static String authToken = null;

    public static void main(String[] args) {
        System.out.println("♕ Welcome to Chess Client! Type 'help' for available commands.");
        preloginUI();
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
                    System.out.println("Observe feature will be implemented in the next phase.");
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
        System.out.println("  join <gameId> <color> - Join a game as white or black");
        System.out.println("  observe <gameId> - Observe a game (coming soon)");
        System.out.println("  logout - Logout of your account");
        System.out.println("  quit - Exit the application");
    }

    private static void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        try {
            authToken = serverFacade.register(username, password, email);
            System.out.println("Registration successful! Logged in as " + username);
            System.out.println("Updated authToken: " + authToken); // Debug
        } catch (IOException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            authToken = serverFacade.login(username, password);
            System.out.println("Login successful!");
        } catch (IOException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static void logout() {
        try {
            System.out.println("Logout authToken: " + authToken); // Debug
            serverFacade.logout(authToken);
            System.out.println("Logged out successfully.");
            authToken = null;
        } catch (IOException e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private static void createGame() {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();

        try {
            serverFacade.createGame(authToken, gameName);
            System.out.println("Game '" + gameName + "' created successfully.");
        } catch (IOException e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }

    private static void listGames() {
        try {
            String games = serverFacade.listGames(authToken);
            System.out.println("Available games:\n" + games);
        } catch (IOException e) {
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private static void joinGame() {
        System.out.print("Enter game ID: ");
        int gameId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter color (white/black): ");
        String color = scanner.nextLine();

        try {
            serverFacade.joinGame(authToken, gameId, color);
            System.out.println("Joined game " + gameId + " as " + color + ".");
        } catch (IOException e) {
            System.out.println("Failed to join game: " + e.getMessage());
        }
    }

    // Placeholder method for chessboard display
    private static void displayChessBoard() {
        // Print a basic chessboard structure, customizable as needed
        System.out.println("Chessboard goes here. Display both perspectives.");
    }
}
