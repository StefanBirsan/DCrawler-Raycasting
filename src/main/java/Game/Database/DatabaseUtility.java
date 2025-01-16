package Game.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtility {
    private static final String URL = "jdbc:mysql://localhost:3306/dungeoncrawler";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveUser(String username, String password) {
        String query = "INSERT INTO players (PlayerName, Password, Experience) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            Random random = new Random();
            int randomExperience = random.nextInt(1000);
            stmt.setInt(3, randomExperience);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> retrieveByUsername(String username) {
        String query = "SELECT * FROM players WHERE PlayerName = ?";
        Map<String, Object> playerDetails = new HashMap<>();

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username); // Set the PlayerName
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                playerDetails.put("PlayerID", rs.getInt("PlayerID"));
                playerDetails.put("PlayerName", rs.getString("PlayerName"));
                playerDetails.put("Level", rs.getInt("Level"));
                playerDetails.put("Experience", rs.getInt("Experience"));
                playerDetails.put("Password", rs.getString("Password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerDetails.isEmpty() ? null : playerDetails;
    }
}