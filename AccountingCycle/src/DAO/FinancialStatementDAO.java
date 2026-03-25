package DAO;

import Model.FinancialStatement;
import DBConnector.DBConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinancialStatementDAO {

    // ✅ Add new financial statement
    public static boolean addFinancialStatement(FinancialStatement fs) {
        String sql = """
            INSERT INTO financial_statement 
            (company_id, statement_type, period_start, period_end, 
             total_revenue, total_expense, net_income, assets, liabilities, equity)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fs.getCompanyId());
            stmt.setString(2, fs.getStatementType());
            stmt.setDate(3, Date.valueOf(fs.getPeriodStart()));
            stmt.setDate(4, Date.valueOf(fs.getPeriodEnd()));
            stmt.setDouble(5, fs.getTotalRevenue());
            stmt.setDouble(6, fs.getTotalExpense());
            stmt.setDouble(7, fs.getNetIncome());
            stmt.setDouble(8, fs.getAssets());
            stmt.setDouble(9, fs.getLiabilities());
            stmt.setDouble(10, fs.getEquity());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Get all financial statements for a company
    public static List<FinancialStatement> getStatementsByCompany(int companyId) {
        List<FinancialStatement> list = new ArrayList<>();
        String sql = "SELECT * FROM financial_statement WHERE company_id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new FinancialStatement(
                        rs.getInt("statement_id"),
                        rs.getInt("company_id"),
                        rs.getString("statement_type"),
                        rs.getDate("period_start").toLocalDate(),
                        rs.getDate("period_end").toLocalDate(),
                        rs.getDouble("total_revenue"),
                        rs.getDouble("total_expense"),
                        rs.getDouble("net_income"),
                        rs.getDouble("assets"),
                        rs.getDouble("liabilities"),
                        rs.getDouble("equity")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ✅ Generate Income Statement
    public static FinancialStatement generateIncomeStatement(int companyId) {
        double totalRevenue = 0;
        double totalExpense = 0;
        double costOfGoodsSold = 0;
        double salesReturns = 0;
        double salesDiscount = 0;

        String sql = """
            SELECT account_name, debit_total, credit_total
            FROM trial_balance
            WHERE company_id = ?
        """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String account = rs.getString("account_name").toLowerCase();
                double debit = rs.getDouble("debit_total");
                double credit = rs.getDouble("credit_total");

                if (account.contains("revenue") || account.contains("sales") || account.contains("income")) {
                    totalRevenue += credit;
                } else if (account.contains("cost of goods sold") || account.contains("cogs")) {
                    costOfGoodsSold += debit;
                } else if (account.contains("sales return") || account.contains("allowance")) {
                    salesReturns += debit;
                } else if (account.contains("discount")) {
                    salesDiscount += debit;
                } else if (account.contains("expense") || account.contains("utilities") ||
                           account.contains("salaries") || account.contains("rent") ||
                           account.contains("supplies") || account.contains("insurance")) {
                    totalExpense += debit;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        double grossProfit = totalRevenue - (costOfGoodsSold + salesReturns + salesDiscount);
        double netIncome = grossProfit - totalExpense;

        return new FinancialStatement(
                0, companyId, "Income Statement",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                totalRevenue, totalExpense, netIncome,
                0, 0, 0,
                costOfGoodsSold, salesReturns, salesDiscount
        );
    }

    // ✅ Generate Balance Sheet
    public static FinancialStatement generateBalanceSheet(int companyId) {
        double assets = 0, liabilities = 0, equity = 0;

        String sql = """
            SELECT account_name, debit_total, credit_total
            FROM trial_balance
            WHERE company_id = ?
        """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String accountRaw = rs.getString("account_name");
                String account = accountRaw == null ? "" : accountRaw.toLowerCase().trim();
                double debit = rs.getDouble("debit_total");
                double credit = rs.getDouble("credit_total");

                if (account.matches(".*\\b(payable|loan|liabilit|mortgage|unearned)\\b.*")) {
                    liabilities += (credit - debit);
                } else if (account.matches(".*\\b(capital|owner|equity|retained)\\b.*")) {
                    equity += (credit - debit);
                } else if (account.matches(".*\\b(drawing|withdrawal)\\b.*")) {
                    equity -= (debit - credit);
                } else if (account.matches(".*\\b(cash|receiv|inventory|equipment|asset|prepaid|suppl|building|land|investment|note receivable|property|furniture|vehicle)\\b.*")) {
                    assets += (debit - credit);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        FinancialStatement income = generateIncomeStatement(companyId);
        double netIncome = income.getNetIncome();
        equity += netIncome;

        return new FinancialStatement(
                0, companyId, "Balance Sheet",
                LocalDate.now().withDayOfYear(1),
                LocalDate.now(),
                0, 0, netIncome,
                assets, liabilities, equity,
                income.getCostOfGoodsSold(), income.getSalesReturns(), income.getSalesDiscount()
        );
    }
}
