package DAO;

import Model.Company;
import DBConnector.DBConnector;
import java.sql.*;
import java.util.*;

public class CompanyDAO {

    public static boolean addCompany(String companyName) {
        String sql = "INSERT INTO company (company_name) VALUES (?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, companyName);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Failed to add company: " + e.getMessage());
            return false;
        }
    }

    public static List<Company> getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT company_id, company_name FROM company";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Company c = new Company(
                        rs.getInt("company_id"),
                        rs.getString("company_name")
                );
                companies.add(c);
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to load companies: " + e.getMessage());
        }
        return companies;
    }

    public static boolean deleteCompany(int companyId) {
        String sql = "DELETE FROM company WHERE company_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Failed to delete company: " + e.getMessage());
            return false;
        }
    }
}
