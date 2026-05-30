
package dao;

import model.User;
import config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private Connection conn = DBConnection.getInstance().getConnection();

    public User login(String username, String password) {
        String sql = "SELECT * FROM User WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getInt("org_id")
                );
            }
        } catch (SQLException e) {
            System.err.println("login: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE role='COORDINATOR'";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getInt("org_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("getAllUsers: " + e.getMessage());
        }
        return list;
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, full_name, email, role, org_id) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole());
            ps.setInt(6, user.getOrgId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addUser: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE User SET username=?, password=?, full_name=?, email=?, role=?, org_id=? WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole());
            ps.setInt(6, user.getOrgId());
            ps.setInt(7, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateUser: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM User WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteUser: " + e.getMessage());
            return false;
        }
    }

    public boolean isUsernameUnique(String username, int excludeId) {
        String sql = "SELECT user_id FROM users WHERE username=? AND user_id!=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, excludeId);
            return !ps.executeQuery().next();
        } catch (SQLException e) {
            return true;
        }
    }

    public boolean isEmailUnique(String email, int excludeId) {
        String sql = "SELECT user_id FROM User WHERE email=? AND user_id!=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            return !ps.executeQuery().next();
        } catch (SQLException e) {
            return true;
        }
    }

    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE User SET password=? WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("changePassword: " + e.getMessage());
            return false;
        }
    }
}
