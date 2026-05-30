package dao;

import model.AidDistribution;
import config.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AidDistributionDAO {

    private Connection conn =
            DBConnection.getInstance().getConnection();

    public List<AidDistribution> getAllDistributions() {

        List<AidDistribution> list =
                new ArrayList<>();

        String sql =
                "SELECT ad.*, " +
                "f.household_name, " +
                "o.name as org_name, " +
                "u.full_name " +
                "FROM AidDistribution ad " +
                "JOIN Family f ON ad.family_id = f.family_id " +
                "JOIN Organization o ON ad.org_id = o.org_id " +
                "JOIN User u ON ad.distributed_by = u.user_id";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println(
                    "getAllDistributions: "
                    + e.getMessage()
            );
        }

        return list;
    }

    public List<AidDistribution> getDistributionsByOrg(int orgId) {

        List<AidDistribution> list =
                new ArrayList<>();

        String sql =
                "SELECT ad.*, " +
                "f.household_name, " +
                "o.name as org_name, " +
                "u.full_name " +
                "FROM AidDistribution ad " +
                "JOIN Family f ON ad.family_id = f.family_id " +
                "JOIN Organization o ON ad.org_id = o.org_id " +
                "JOIN User u ON ad.distributed_by = u.user_id " +
                "WHERE ad.org_id=?";

        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, orgId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {

            System.err.println(
                    "getDistributionsByOrg: "
                    + e.getMessage()
            );
        }

        return list;
    }

    public AidDistribution checkDuplicate(int familyId) {

    String sql =
            "SELECT ad.*, " +
            "f.household_name, " +
            "o.name as org_name, " +
            "u.full_name " +
            "FROM AidDistribution ad " +
            "JOIN Family f ON ad.family_id = f.family_id " +
            "JOIN Organization o ON ad.org_id = o.org_id " +
            "JOIN User u ON ad.distributed_by = u.user_id " +
            "WHERE ad.family_id=? " +
            "AND DATEDIFF(CURDATE(), ad.distribution_date) <= 30 " +
            "ORDER BY ad.distribution_date DESC LIMIT 1";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, familyId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return mapRow(rs);
        }

    } catch (SQLException e) {
        System.err.println("checkDuplicate: " + e.getMessage());
    }

    return null;
}

    public boolean addDistribution(
            AidDistribution dist
    ) {

        String sql =
                "INSERT INTO AidDistribution " +
                "(family_id, org_id, distributed_by, aid_type, distribution_date) " +
                "VALUES (?,?,?,?,?)";

        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, dist.getFamilyId());

            ps.setInt(2, dist.getOrgId());

            ps.setInt(3, dist.getDistributedBy());

            ps.setString(4, dist.getAidType());

            ps.setDate(
                    5,
                    Date.valueOf(
                            dist.getDistributionDate()
                    )
            );

            if (ps.executeUpdate() > 0) {

                updateLastAidDate(
                        dist.getFamilyId(),
                        dist.getDistributionDate()
                );

                return true;
            }

        } catch (SQLException e) {

            System.err.println(
                    "addDistribution: "
                    + e.getMessage()
            );
        }

        return false;
    }

    private void updateLastAidDate(
            int familyId,
            LocalDate date
    ) {

        String sql =
                "UPDATE Family " +
                "SET last_aid_date=? " +
                "WHERE family_id=?";

        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setDate(
                    1,
                    Date.valueOf(date)
            );

            ps.setInt(2, familyId);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.err.println(
                    "updateLastAidDate: "
                    + e.getMessage()
            );
        }
    }

    public int getTotalServedFamilies() {

        String sql =
                "SELECT COUNT(DISTINCT family_id) " +
                "FROM AidDistribution";

        try (Statement st =
                     conn.createStatement();

             ResultSet rs =
                     st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {

            System.err.println(
                    "getTotalServedFamilies: "
                    + e.getMessage()
            );
        }

        return 0;
    }

    public int getServedFamiliesByOrg(int orgId) {

        String sql =
                "SELECT COUNT(DISTINCT family_id) " +
                "FROM AidDistribution " +
                "WHERE org_id=?";

        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, orgId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {

            System.err.println(
                    "getServedFamiliesByOrg: "
                    + e.getMessage()
            );
        }

        return 0;
    }

    private AidDistribution mapRow(
            ResultSet rs
    ) throws SQLException {

        AidDistribution ad =
                new AidDistribution(

                        rs.getInt("distribution_id"),

                        rs.getInt("family_id"),

                        rs.getInt("org_id"),

                        rs.getInt("distributed_by"),

                        rs.getString("aid_type"),

                        rs.getDate("distribution_date")
                                .toLocalDate()
                );

        ad.setFamilyName(
                rs.getString("household_name")
        );

        ad.setOrgName(
                rs.getString("org_name")
        );

        ad.setCoordinatorName(
                rs.getString("full_name")
        );

        return ad;
    }
}