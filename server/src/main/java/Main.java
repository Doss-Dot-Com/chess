package server;

import spark.Spark;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = 8080;
        server.run(port);

        System.out.println("Server started on port " + port);
    }
}