package DAO;

import Model.GeneralLedger;
import DBConnector.DBConnector;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class GeneralLedgerDAO {
    
    // ✅ Original method (for backward compatibility)
    public static boolean addLedgerEntry(GeneralLedger gl) {
        try (Connection conn = DBConnector.getConnection()) {
            return addLedgerEntry(gl, conn);
        } catch (SQLException e) {
            System.err.println("❌ Error in addLedgerEntry: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ New method with connection parameter (for transactions)
    public static boolean addLedgerEntry(GeneralLedger gl, Connection conn) {
        String sql = "INSERT INTO general_ledger (company_id, account_name, transaction_date, debit, credit, balance) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 🔹 Compute running balance before insert
            double prevBalance = getLastBalance(gl.getCompanyId(), gl.getAccountName(), conn);
            double newBalance = prevBalance + gl.getDebit() - gl.getCredit();
            gl.setBalance(newBalance);
            
            System.out.println("📊 Ledger Entry Debug:");
            System.out.println("   Account: " + gl.getAccountName());
            System.out.println("   Previous Balance: " + prevBalance);
            System.out.println("   Debit: " + gl.getDebit());
            System.out.println("   Credit: " + gl.getCredit());
            System.out.println("   New Balance: " + newBalance);
            
            stmt.setInt(1, gl.getCompanyId());
            stmt.setString(2, gl.getAccountName());
            stmt.setDate(3, Date.valueOf(gl.getTransactionDate()));
            stmt.setDouble(4, gl.getDebit());
            stmt.setDouble(5, gl.getCredit());
            stmt.setDouble(6, gl.getBalance());
            
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int ledgerId = keys.getInt(1);
                    System.out.println("   ✅ Ledger entry inserted with ID: " + ledgerId);
                }
                return true;
            } else {
                System.err.println("   ❌ No rows inserted!");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ SQL Error in addLedgerEntry: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ Get last balance (with connection parameter for transactions)
    private static double getLastBalance(int companyId, String accountName, Connection conn) {
        String sql = "SELECT balance FROM general_ledger WHERE company_id = ? AND account_name = ? ORDER BY transaction_date DESC, ledger_id DESC LIMIT 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            stmt.setString(2, accountName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                System.out.println("   📌 Found previous balance for " + accountName + ": " + balance);
                return balance;
            } else {
                System.out.println("   📌 No previous balance for " + accountName + ", starting from 0.00");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting last balance: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.00; // Default if no previous balance
    }
    
    // ✅ Original method (for backward compatibility)
    private static double getLastBalance(int companyId, String accountName) {
        try (Connection conn = DBConnector.getConnection()) {
            return getLastBalance(companyId, accountName, conn);
        } catch (SQLException e) {
            System.err.println("❌ Error in getLastBalance: " + e.getMessage());
            e.printStackTrace();
            return 0.00;
        }
    }
    
    public static List<GeneralLedger> getLedgerByCompany(int companyId) {
        List<GeneralLedger> list = new ArrayList<>();
        String sql = "SELECT * FROM general_ledger WHERE company_id = ? ORDER BY account_name, transaction_date";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                list.add(new GeneralLedger(
                        rs.getInt("ledger_id"),
                        rs.getInt("company_id"),
                        rs.getString("account_name"),
                        rs.getDate("transaction_date").toLocalDate(),
                        rs.getDouble("debit"),
                        rs.getDouble("credit"),
                        rs.getDouble("balance")
                ));
                count++;
            }
            
            System.out.println("📋 Loaded " + count + " ledger entries for company " + companyId);
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading ledger: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    public static boolean deleteLedgerEntry(int ledgerId) {
        String sql = "DELETE FROM general_ledger WHERE ledger_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ledgerId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}