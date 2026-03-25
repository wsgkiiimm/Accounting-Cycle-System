package UI;

import DAO.FinancialStatementDAO;
import Model.FinancialStatement;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;

public class FinancialStatementForm extends JFrame {
    private int companyId;
    private String companyName;
    private JTable table;
    private JComboBox<String> cmbType;
    private JLabel lblSummary;

    public FinancialStatementForm(int companyId, String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;
        initUI();
    }

    private void initUI() {
        setTitle("Financial Statement - " + companyName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(UIStyle.createHeader("📊 Financial Statement", companyName), BorderLayout.NORTH);
        add(UIStyle.createFooter(), BorderLayout.SOUTH);

        JPanel pnlBody = new JPanel(new BorderLayout(10, 10));
        pnlBody.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        JLabel lblType = new JLabel("Statement Type:");
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 16));

        cmbType = new JComboBox<>(new String[]{
                "Income Statement", "Balance Sheet"
        });
        cmbType.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JButton btnGenerate = new JButton("Generate Statement");
        UIStyle.styleButton(btnGenerate);

        pnlTop.add(lblType);
        pnlTop.add(cmbType);
        pnlTop.add(btnGenerate);
        pnlBody.add(pnlTop, BorderLayout.NORTH);

        table = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"Account", "Amount"}
        ));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.setFillsViewportHeight(true);

        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightAlign);

        JScrollPane scrollPane = new JScrollPane(table);
        pnlBody.add(scrollPane, BorderLayout.CENTER);

        lblSummary = new JLabel("Select a statement to generate.", SwingConstants.CENTER);
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSummary.setForeground(new Color(0x2E86C1));
        lblSummary.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        pnlBody.add(lblSummary, BorderLayout.SOUTH);

        add(pnlBody, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        JButton btnBack = new JButton("⬅ Back to Accounting Cycle");
        UIStyle.styleButton(btnBack);
        pnlBottom.add(btnBack);
        add(pnlBottom, BorderLayout.PAGE_END);

        btnBack.addActionListener(e -> {
            new AccountingCycleForm(companyId, companyName).setVisible(true);
            dispose();
        });

        btnGenerate.addActionListener(e -> generateStatement());
    }

    private void generateStatement() {
        String type = (String) cmbType.getSelectedItem();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,##0.00");

        try {
            if ("Income Statement".equals(type)) {
                FinancialStatement fs = FinancialStatementDAO.generateIncomeStatement(companyId);

                // ===== Revenue Section =====
                model.addRow(new Object[]{"Total Revenue", "₱" + df.format(fs.getTotalRevenue())});

                // ===== Cost of Goods Sold (COGS) Section =====
                double cogs = fs.getCostOfGoodsSold();
                if (cogs > 0) {
                    model.addRow(new Object[]{"Cost of Goods Sold", "₱" + df.format(cogs)});
                    double grossProfit = fs.getTotalRevenue() - cogs;
                    model.addRow(new Object[]{"Gross Profit", "₱" + df.format(grossProfit)});
                }

                // ===== Sales Returns / Discounts if available =====
                if (fs.getSalesReturns() > 0) {
                    model.addRow(new Object[]{"Sales Returns and Allowances", "-₱" + df.format(fs.getSalesReturns())});
                }
                if (fs.getSalesDiscount() > 0) {
                    model.addRow(new Object[]{"Sales Discounts", "-₱" + df.format(fs.getSalesDiscount())});
                }

                // ===== Expense Section =====
                model.addRow(new Object[]{"Total Expense", "₱" + df.format(fs.getTotalExpense())});
                model.addRow(new Object[]{"Net Income", "₱" + df.format(fs.getNetIncome())});

                // ===== Summary Display =====
                if (fs.getNetIncome() >= 0) {
                    lblSummary.setForeground(new Color(0x1E8449));
                    lblSummary.setText("✅ Net Income: ₱" + df.format(fs.getNetIncome()));
                } else {
                    lblSummary.setForeground(Color.RED);
                    lblSummary.setText("⚠ Net Loss: ₱" + df.format(Math.abs(fs.getNetIncome())));
                }

                FinancialStatementDAO.addFinancialStatement(fs);

            } else if ("Balance Sheet".equals(type)) {
                FinancialStatement fs = FinancialStatementDAO.generateBalanceSheet(companyId);

                double totalRight = fs.getLiabilities() + fs.getEquity();
                double diff = fs.getAssets() - totalRight;

                model.addRow(new Object[]{"Assets", "₱" + df.format(fs.getAssets())});
                model.addRow(new Object[]{"Liabilities", "₱" + df.format(fs.getLiabilities())});
                model.addRow(new Object[]{"Equity", "₱" + df.format(fs.getEquity())});

                if (Math.abs(diff) < 1) {
                    lblSummary.setForeground(new Color(0x1E8449));
                    lblSummary.setText("✅ Balanced: Assets = Liabilities + Equity (" +
                            "₱" + df.format(fs.getAssets()) + ")");
                } else {
                    lblSummary.setForeground(Color.RED);
                    lblSummary.setText("⚠ Unbalanced by ₱" + df.format(diff) +
                            " (Assets: ₱" + df.format(fs.getAssets()) +
                            ", L+E: ₱" + df.format(totalRight) + ")");
                }

                FinancialStatementDAO.addFinancialStatement(fs);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating statement:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
