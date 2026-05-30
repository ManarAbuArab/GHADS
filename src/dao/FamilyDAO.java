
package dao;

import model.Family;
import config.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FamilyDAO {

    private Connection conn = DBConnection.getInstance().getConnection();

    public List<Family> getAllFamilies() {
        List<Family> list = new ArrayList<>();
        String sql = "SELECT * FROM Family";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("getAllFamilies: " + e.getMessage());
        }
        return list;
    }

    public List<Family> getUnservedFamilies() {
        List<Family> list = new ArrayList<>();
        String sql = "SELECT * FROM Family WHERE family_id NOT IN (SELECT DISTINCT family_id FROM AidDistribution)";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("getUnservedFamilies: " + e.getMessage());
        }
        return list;
    }

    public List<Family> getFamiliesByVulnerability() {
        List<Family> list = new ArrayList<>();
        String sql = "SELECT * FROM Family ORDER BY CASE vulnerability_level " +
                     "WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 END";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("getFamiliesByVulnerability: " + e.getMessage());
        }
        return list;
    }

    public boolean addFamily(Family family) {
        String sql = "INSERT INTO Family (household_name, phone, location, family_size, national_id, vulnerability_level, registration_date) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, family.getHouseholdName());
            ps.setString(2, family.getPhone());
            ps.setString(3, family.getLocation());
            ps.setInt(4, family.getFamilySize());
            ps.setString(5, family.getNationalId());
            ps.setString(6, family.getVulnerabilityLevel());
            ps.setDate(7, Date.valueOf(family.getRegistrationDate()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addFamily: " + e.getMessage());
            return false;
        }
    }

    public boolean updateFamily(Family family) {
        String sql = "UPDATE Family SET household_name=?, phone=?, location=?, family_size=?, national_id=?, vulnerability_level=? WHERE family_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, family.getHouseholdName());
            ps.setString(2, family.getPhone());
            ps.setString(3, family.getLocation());
            ps.setInt(4, family.getFamilySize());
            ps.setString(5, family.getNationalId());
            ps.setString(6, family.getVulnerabilityLevel());
            ps.setInt(7, family.getFamilyId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateFamily: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteFamily(int familyId) {
        String sql = "DELETE FROM Family WHERE family_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, familyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteFamily: " + e.getMessage());
            return false;
        }
    }

    public boolean isNationalIdUnique(String nationalId, int excludeId) {
        String sql = "SELECT family_id FROM Family WHERE national_id=? AND family_id!=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nationalId);
            ps.setInt(2, excludeId);
            return !ps.executeQuery().next();
        } catch (SQLException e) {
            return true;
        }
    }

    private Family mapRow(ResultSet rs) throws SQLException {
        Date lastAid = rs.getDate("last_aid_date");
        return new Family(
            rs.getInt("family_id"),
            rs.getString("household_name"),
            rs.getString("phone"),
            rs.getString("location"),
            rs.getInt("family_size"),
            rs.getString("national_id"),
            rs.getString("vulnerability_level"),
            rs.getDate("registration_date").toLocalDate(),
            lastAid != null ? lastAid.toLocalDate() : null
        );
    }
}