package server;

import dataaccess.InMemoryDataAccess;
import service.UserService;

public class Server {
    public static void main(String[] args) {
        InMemoryDataAccess dataAccess = new InMemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        UserHandler userHandler = new UserHandler(userService);

        userHandler.register();
        userHandler.login();
        userHandler.logout();

        System.out.println("Server started on port 8080");
    }
}
