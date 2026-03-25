package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import DAO.TrialBalanceDAO;
import Model.TrialBalance;

public class TrialBalanceForm extends JFrame {
    private final int companyId;
    private final String companyName;
    private final JTable tblTrial;
    private final JTextField txtDebit;
    private final JTextField txtCredit;
    private final JLabel lblBalanceStatus;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // temporary test data
            int testCompanyId = 1;
            String testCompanyName = "Demo Company";
            new TrialBalanceForm(testCompanyId, testCompanyName).setVisible(true);
        });
    }


    public TrialBalanceForm(int companyId, String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;
        
        

        // Initialize UI components
        setTitle("Trial Balance - " + companyName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        add(UIStyle.createHeader("Trial Balance", companyName), BorderLayout.NORTH);

        // ===== BODY =====
        JPanel pnlBody = new JPanel(new BorderLayout(20, 20));
        pnlBody.setBackground(UIStyle.BG_COLOR);
        pnlBody.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        // ===== TABLE =====
        // Custom non-editable model
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new String[]{"Account Title", "Debit", "Credit"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTrial = new JTable(model);
        tblTrial.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        tblTrial.setRowHeight(35);
        tblTrial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Align debit/credit columns to right and set preferred widths
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tblTrial.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        tblTrial.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblTrial.getColumnModel().getColumn(0).setPreferredWidth(400);
        tblTrial.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblTrial.getColumnModel().getColumn(2).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(tblTrial);
        pnlBody.add(scrollPane, BorderLayout.CENTER);

        // ===== TOTALS PANEL =====
        JPanel pnlTotalsContainer = new JPanel(new BorderLayout());
        pnlTotalsContainer.setBackground(Color.WHITE);
        pnlTotalsContainer.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // Separator
        JSeparator separator = new JSeparator();
        pnlTotalsContainer.add(separator, BorderLayout.NORTH);

        // Totals panel
        JPanel pnlTotals = new JPanel(new GridLayout(1, 4, 50, 10));
        pnlTotals.setBackground(Color.WHITE);
        pnlTotals.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JLabel lblDebit = new JLabel("Total Debit:", SwingConstants.RIGHT);
        lblDebit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlTotals.add(lblDebit);

        txtDebit = new JTextField("₱0.00");
        txtDebit.setEditable(false);
        txtDebit.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY));
        txtDebit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtDebit.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlTotals.add(txtDebit);

        JLabel lblCredit = new JLabel("Total Credit:", SwingConstants.RIGHT);
        lblCredit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlTotals.add(lblCredit);

        txtCredit = new JTextField("₱0.00");
        txtCredit.setEditable(false);
        txtCredit.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY));
        txtCredit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtCredit.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlTotals.add(txtCredit);

        lblBalanceStatus = new JLabel(" ", SwingConstants.CENTER);
        lblBalanceStatus.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBalanceStatus.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        pnlTotalsContainer.add(pnlTotals, BorderLayout.CENTER);
        pnlTotalsContainer.add(lblBalanceStatus, BorderLayout.SOUTH);

        pnlBody.add(pnlTotalsContainer, BorderLayout.SOUTH);
        add(pnlBody, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JButton btnBack = new JButton("⬅ Back to Cycle");
        UIStyle.styleButton(btnBack);
        pnlFooter.add(btnBack, BorderLayout.WEST);

        JButton btnGenerate = new JButton("Generate Trial Balance");
        UIStyle.styleButton(btnGenerate);
        pnlFooter.add(btnGenerate, BorderLayout.EAST);

        JLabel lblFooter = new JLabel("© 2025 Accounting Cycle System | Developed by IT BOYS", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblFooter.setForeground(new Color(100, 100, 100));
        pnlFooter.add(lblFooter, BorderLayout.SOUTH);

        add(pnlFooter, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        btnBack.addActionListener(e -> {
            new AccountingCycleForm(companyId, companyName).setVisible(true);
            dispose();
        });

        btnGenerate.addActionListener(e -> {
            // Generate and reload data
            System.out.println("[TrialBalanceForm] Generate button clicked for companyId = " + companyId);
            TrialBalanceDAO.saveGeneratedTrialBalance(companyId);
            loadTrialBalanceData();
            JOptionPane.showMessageDialog(this, "Trial Balance successfully generated!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        // Load data on open — if empty, try to auto-generate once and reload
        loadTrialBalanceData();
    }

    /**
     * Loads trial balance from DB and updates totals + balance status.
     * If no records found, it will attempt to call saveGeneratedTrialBalance once to auto-populate from ledger.
     */
    private void loadTrialBalanceData() {
        System.out.println("[TrialBalanceForm] Loading Trial Balance for companyId = " + companyId);
        List<TrialBalance> tbList = TrialBalanceDAO.getTrialBalanceByCompany(companyId);

        // If there are no rows saved in trial_balance, attempt a one-time auto-generation.
        if (tbList.isEmpty()) {
            System.out.println("[TrialBalanceForm] No saved trial_balance rows found for companyId = " + companyId + ". Attempting auto-generation from ledger...");
            TrialBalanceDAO.saveGeneratedTrialBalance(companyId);
            // reload
            tbList = TrialBalanceDAO.getTrialBalanceByCompany(companyId);
            System.out.println("[TrialBalanceForm] After generation, rows found: " + tbList.size());
        } else {
            System.out.println("[TrialBalanceForm] Found saved trial_balance rows: " + tbList.size());
        }

        DefaultTableModel model = (DefaultTableModel) tblTrial.getModel();
        model.setRowCount(0);

        double totalDebit = 0;
        double totalCredit = 0;

        for (TrialBalance tb : tbList) {
            model.addRow(new Object[]{
                    tb.getAccountName(),
                    String.format("₱%,.2f", tb.getDebitTotal()),
                    String.format("₱%,.2f", tb.getCreditTotal())
            });
            totalDebit += tb.getDebitTotal();
            totalCredit += tb.getCreditTotal();
        }

        txtDebit.setText(String.format("₱%,.2f", totalDebit));
        txtCredit.setText(String.format("₱%,.2f", totalCredit));

        // ===== BALANCE CHECK =====
        if (tbList.isEmpty()) {
            lblBalanceStatus.setText("⚠ No trial balance records found.");
            lblBalanceStatus.setForeground(new Color(180, 120, 0));
        } else if (Math.abs(totalDebit - totalCredit) < 0.005) {
            lblBalanceStatus.setText("✅ Balanced");
            lblBalanceStatus.setForeground(new Color(0, 128, 0));
        } else {
            lblBalanceStatus.setText("⚠ Not Balanced");
            lblBalanceStatus.setForeground(Color.RED);
        }
    }
}
