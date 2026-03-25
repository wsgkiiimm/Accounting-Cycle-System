package UI;

import javax.swing.*;
import java.awt.*;

public class AccountingCycleForm extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int companyId;
    private String companyName;

    public AccountingCycleForm(int companyId, String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;
        initUI();
    }

    private void initUI() {
        setTitle("Accounting Cycle - " + companyName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));

        // ===== HEADER =====
        add(UIStyle.createHeader("Accounting Cycle", companyName), BorderLayout.NORTH);

        // ===== CENTER PANEL =====
        JPanel pnlCenter = new JPanel(new GridLayout(2, 2, 40, 40));
        pnlCenter.setBackground(UIStyle.BG_COLOR);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(60, 120, 60, 120));

        JButton btnJournal = createCycleButton("📘 Journal Entries", "Record all transactions.");
        JButton btnLedger = createCycleButton("📒 General Ledger", "Post entries to accounts.");
        JButton btnTrial = createCycleButton("📊 Trial Balance", "Verify debit = credit.");
        JButton btnStatement = createCycleButton("📑 Financial Statements", "Prepare final reports.");

        pnlCenter.add(btnJournal);
        pnlCenter.add(btnLedger);
        pnlCenter.add(btnTrial);
        pnlCenter.add(btnStatement);

        add(pnlCenter, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JButton btnBack = new JButton("⬅ Back to Dashboard");
        UIStyle.styleButton(btnBack);
        pnlFooter.add(btnBack, BorderLayout.WEST);

        JLabel lblFooter = new JLabel("© 2025 Accounting Cycle System | Developed by IT BOYS", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblFooter.setForeground(new Color(100, 100, 100));
        pnlFooter.add(lblFooter, BorderLayout.SOUTH);

        add(pnlFooter, BorderLayout.SOUTH);

        // ===== EVENT HANDLERS =====
        btnJournal.addActionListener(e -> openForm("JournalEntryForm"));
        btnLedger.addActionListener(e -> openForm("GeneralLedgerForm"));
        btnTrial.addActionListener(e -> openForm("TrialBalanceForm"));
        btnStatement.addActionListener(e -> openForm("FinancialStatementForm"));
        btnBack.addActionListener(e -> {
            new CompanyDashboard(companyId, companyName).setVisible(true);
            dispose();
        });
    }

    private JButton createCycleButton(String title, String subtitle) {
        JButton btn = new JButton("<html><center><b>" + title + "</b><br><span style='font-size:12px;color:gray;'>"
                + subtitle + "</span></center></html>");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true),
                BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        btn.setPreferredSize(new Dimension(260, 150));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIStyle.ACCENT);
                btn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }
        });

        return btn;
    }

    private void openForm(String formName) {
        switch (formName) {
            case "JournalEntryForm":
                new JournalEntryForm(companyId, companyName).setVisible(true);
                break;
            case "GeneralLedgerForm":
                new GeneralLedgerForm(companyId, companyName).setVisible(true);
                break;
            case "TrialBalanceForm":
                new TrialBalanceForm(companyId, companyName).setVisible(true);
                break;
            case "FinancialStatementForm":
                new FinancialStatementForm(companyId, companyName).setVisible(true);
                break;
        }
        dispose();
    }
}
