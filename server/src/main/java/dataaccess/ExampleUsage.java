package dataaccess;

public class ExampleUsage {

    public void example() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1 + 1")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println("Result of 1 + 1: " + rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
