package DAO;

import Model.TrialBalance;
import DBConnector.DBConnector;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * TrialBalanceDAO
 * - generateTrialBalanceFromLedger: builds trial balance from general_ledger sums
 *   and places the result on the correct side (debit or credit) according to
 *   account type (asset/expense = debit-normal; revenue/liability/equity = credit-normal).
 * - saveGeneratedTrialBalance: clears and saves generated rows into trial_balance table.
 */
public class TrialBalanceDAO {

    public static boolean addTrialBalance(TrialBalance tb) {
        String sql = "INSERT INTO trial_balance (company_id, account_name, debit_total, credit_total) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tb.getCompanyId());
            stmt.setString(2, tb.getAccountName());
            stmt.setDouble(3, tb.getDebitTotal());
            stmt.setDouble(4, tb.getCreditTotal());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<TrialBalance> getTrialBalanceByCompany(int companyId) {
        List<TrialBalance> list = new ArrayList<>();
        String sql = "SELECT * FROM trial_balance WHERE company_id = ? ORDER BY account_name";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new TrialBalance(
                        rs.getInt("trial_id"),
                        rs.getInt("company_id"),
                        rs.getString("account_name"),
                        rs.getDouble("debit_total"),
                        rs.getDouble("credit_total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Build a trial balance from general_ledger sums.
     * Algorithm:
     *  - For each account_name, get SUM(debit) and SUM(credit)
     *  - Determine account normal side (debit-normal or credit-normal) by keywords
     *  - Compute natural balance and put the amount on correct column:
     *        debit-normal: natural = debitSum - creditSum -> if >0 => debit, else credit = abs(natural)
     *        credit-normal: natural = creditSum - debitSum -> if >0 => credit, else debit = abs(natural)
     *
     *  This yields properly classified debit_total and credit_total values.
     */
    public static List<TrialBalance> generateTrialBalanceFromLedger(int companyId) {
        List<TrialBalance> list = new ArrayList<>();

        String sql = "SELECT account_name, SUM(debit) AS total_debit, SUM(credit) AS total_credit "
                   + "FROM general_ledger "
                   + "WHERE company_id = ? "
                   + "GROUP BY account_name "
                   + "ORDER BY account_name";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String accountName = rs.getString("account_name");
                if (accountName == null) accountName = "Unnamed Account";
                accountName = accountName.trim();

                BigDecimal debitSum = BigDecimal.valueOf(rs.getDouble("total_debit"));
                BigDecimal creditSum = BigDecimal.valueOf(rs.getDouble("total_credit"));

                // Decide account normal side using keywords
                boolean debitNormal = isDebitNormal(accountName.toLowerCase());
                BigDecimal debitTotal = BigDecimal.ZERO;
                BigDecimal creditTotal = BigDecimal.ZERO;

                if (debitNormal) {
                    // natural = debit - credit
                    BigDecimal natural = debitSum.subtract(creditSum);
                    if (natural.compareTo(BigDecimal.ZERO) >= 0) {
                        debitTotal = natural;
                    } else {
                        creditTotal = natural.abs();
                    }
                } else {
                    // credit-normal: natural = credit - debit
                    BigDecimal natural = creditSum.subtract(debitSum);
                    if (natural.compareTo(BigDecimal.ZERO) >= 0) {
                        creditTotal = natural;
                    } else {
                        debitTotal = natural.abs();
                    }
                }

                // Only include non-zero balances (optional)
                // If you want to include zero balances, remove the if check below
                if (debitTotal.compareTo(BigDecimal.ZERO) != 0 || creditTotal.compareTo(BigDecimal.ZERO) != 0) {
                    list.add(new TrialBalance(
                            0,
                            companyId,
                            accountName,
                            debitTotal.doubleValue(),
                            creditTotal.doubleValue()
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Persist generated trial balance: delete old rows for company_id and insert new generated rows.
     */
    public static void saveGeneratedTrialBalance(int companyId) {
        List<TrialBalance> generated = generateTrialBalanceFromLedger(companyId);

        String deleteSql = "DELETE FROM trial_balance WHERE company_id = ?";
        String insertSql = "INSERT INTO trial_balance (company_id, account_name, debit_total, credit_total) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                del.setInt(1, companyId);
                del.executeUpdate();
            }

            try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                for (TrialBalance tb : generated) {
                    ins.setInt(1, tb.getCompanyId());
                    ins.setString(2, tb.getAccountName());
                    ins.setDouble(3, tb.getDebitTotal());
                    ins.setDouble(4, tb.getCreditTotal());
                    ins.addBatch();
                }
                ins.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determine whether an account is debit-normal.
     * This is heuristic-based on common keywords; tweak for your chart of accounts.
     */
    private static boolean isDebitNormal(String accountLower) {
        if (accountLower == null) return true;
        // common debit-normal keywords
        String[] debitKeywords = {
                "cash", "asset", "receivable", "inventory", "equipment", "prepaid", "supplies", "expense", "cost", "deposit"
        };
        for (String k : debitKeywords) {
            if (accountLower.contains(k)) return true;
        }

        // otherwise treat as credit-normal (revenues, payables, loans, capital, equity)
        return false;
    }
}
