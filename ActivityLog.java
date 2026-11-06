/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartstock_inventory_system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ActivityLog {

    private static boolean tableChecked = false;

    public static void ensureTable(Connection con) throws SQLException {
        if (tableChecked) {
            return;
        }
        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS tbl_activity_log ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "users_id INT NOT NULL, "
                    + "activity VARCHAR(255) NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );
        }
        tableChecked = true;
    }

    
    public static void log(String message) {
        int userId = UserSession.getUserId();
        if (userId <= 0) {
            return;
        }

        try (Connection con = DBConnection.connect()) {
            if (con == null) {
                return;
            }
            ensureTable(con);

            try (PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO tbl_activity_log (users_id, activity) VALUES (?, ?)")) {
                pst.setInt(1, userId);
                pst.setString(2, message);
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Activity log failed: " + e.getMessage());
        }
    }
}
