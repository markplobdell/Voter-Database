import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        String dbURL = "jdbc:sqlite:../univ.db";

        System.out.println("Trying to connect...");
        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement stmt = conn.createStatement()) {

            System.out.println("Executing query...");
            ResultSet rset = stmt.executeQuery("SELECT name FROM sqlite_schema WHERE type = 'table'");
            while (rset.next()) {

                System.out.println("Table: " + rset.getString("name"));


            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println("Done!");
    }
}