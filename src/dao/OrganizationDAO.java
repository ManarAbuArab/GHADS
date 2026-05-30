
package dao;

import model.Organization;
import config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrganizationDAO {

    private Connection conn = DBConnection.getInstance().getConnection();

    public List<Organization> getAllOrganizations() {
        List<Organization> list = new ArrayList<>();
        String sql = "SELECT * FROM Organization";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Organization(
                    rs.getInt("org_id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getString("contact_info")
                ));
            }
        } catch (SQLException e) {
            System.err.println("getAllOrganizations: " + e.getMessage());
        }
        return list;
    }

    public boolean addOrganization(Organization org) {
        String sql = "INSERT INTO Organization (name, type, contact_info) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, org.getName());
            ps.setString(2, org.getType());
            ps.setString(3, org.getContactInfo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addOrganization: " + e.getMessage());
            return false;
        }
    }

    public boolean updateOrganization(Organization org) {
        String sql = "UPDATE Organization SET name=?, type=?, contact_info=? WHERE org_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, org.getName());
            ps.setString(2, org.getType());
            ps.setString(3, org.getContactInfo());
            ps.setInt(4, org.getOrgId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateOrganization: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteOrganization(int orgId) {
        String sql = "DELETE FROM Organization WHERE org_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orgId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteOrganization: " + e.getMessage());
            return false;
        }
    }
}