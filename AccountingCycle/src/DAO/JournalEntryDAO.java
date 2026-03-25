package DAO;

import Model.GeneralLedger;
import Model.JournalEntry;
import DBConnector.DBConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class JournalEntryDAO {

    public static boolean addJournalEntry(JournalEntry je) {
        String sql = "INSERT INTO journal_entry (company_id, entry_date, description, debit_account, credit_account, debit_amount, credit_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // ✅ Use transaction
            
            // 🔹 Insert journal entry
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, je.getCompanyId());
                stmt.setString(2, je.getEntryDate());
                stmt.setString(3, je.getDescription());
                stmt.setString(4, je.getDebitAccount());
                stmt.setString(5, je.getCreditAccount());
                stmt.setDouble(6, je.getDebitAmount());
                stmt.setDouble(7, je.getCreditAmount());
                
                int rows = stmt.executeUpdate();
                System.out.println("✅ Journal entry inserted: " + rows + " row(s)");

                // 🔹 Get generated journal ID
                ResultSet keys = stmt.getGeneratedKeys();
                int journalId = 0;
                if (keys.next()) {
                    journalId = keys.getInt(1);
                    System.out.println("✅ Generated journal_id: " + journalId);
                }

                // 🔹 Convert entry date to LocalDate
                LocalDate txnDate = LocalDate.parse(je.getEntryDate());

                // 🔹 Create debit ledger entry
                GeneralLedger debitEntry = new GeneralLedger(
                        0,
                        je.getCompanyId(),
                        je.getDebitAccount(),
                        txnDate,
                        je.getDebitAmount(),
                        0.00,
                        0.00 // balance will be calculated in addLedgerEntry
                );

                // 🔹 Create credit ledger entry
                GeneralLedger creditEntry = new GeneralLedger(
                        0,
                        je.getCompanyId(),
                        je.getCreditAccount(),
                        txnDate,
                        0.00,
                        je.getCreditAmount(),
                        0.00 // balance will be calculated in addLedgerEntry
                );

                // 🔹 Post both to general ledger
                System.out.println("🔄 Posting DEBIT entry to ledger: " + je.getDebitAccount());
                boolean debitSuccess = GeneralLedgerDAO.addLedgerEntry(debitEntry, conn);
                if (!debitSuccess) {
                    System.err.println("❌ Failed to post debit entry to ledger!");
                    conn.rollback();
                    return false;
                }
                System.out.println("✅ Debit entry posted successfully");

                System.out.println("🔄 Posting CREDIT entry to ledger: " + je.getCreditAccount());
                boolean creditSuccess = GeneralLedgerDAO.addLedgerEntry(creditEntry, conn);
                if (!creditSuccess) {
                    System.err.println("❌ Failed to post credit entry to ledger!");
                    conn.rollback();
                    return false;
                }
                System.out.println("✅ Credit entry posted successfully");

                conn.commit(); // ✅ Commit transaction
                System.out.println("✅ Transaction committed successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in addJournalEntry: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error in addJournalEntry: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<JournalEntry> getEntriesByCompany(int companyId) {
        List<JournalEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM journal_entry WHERE company_id = ? ORDER BY entry_date DESC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                entries.add(new JournalEntry(
                        rs.getInt("journal_id"),
                        rs.getInt("company_id"),
                        rs.getString("entry_date"),
                        rs.getString("description"),
                        rs.getString("debit_account"),
                        rs.getString("credit_account"),
                        rs.getDouble("debit_amount"),
                        rs.getDouble("credit_amount")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static boolean updateJournalEntry(JournalEntry je) {
        String sql = "UPDATE journal_entry SET entry_date = ?, description = ?, debit_account = ?, credit_account = ?, debit_amount = ?, credit_amount = ? WHERE journal_id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, je.getEntryDate());
            stmt.setString(2, je.getDescription());
            stmt.setString(3, je.getDebitAccount());
            stmt.setString(4, je.getCreditAccount());
            stmt.setDouble(5, je.getDebitAmount());
            stmt.setDouble(6, je.getCreditAmount());
            stmt.setInt(7, je.getJournalId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteJournalEntry(int id) {
        String sql = "DELETE FROM journal_entry WHERE journal_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<JournalEntry> getAllEntries() {
        List<JournalEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM journal_entry ORDER BY entry_date DESC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new JournalEntry(
                        rs.getInt("journal_id"),
                        rs.getInt("company_id"),
                        rs.getString("entry_date"),
                        rs.getString("description"),
                        rs.getString("debit_account"),
                        rs.getString("credit_account"),
                        rs.getDouble("debit_amount"),
                        rs.getDouble("credit_amount")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}