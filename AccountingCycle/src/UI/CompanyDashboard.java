package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CompanyDashboard extends JFrame {
    private int companyId;
    private String companyName;

    public CompanyDashboard(int companyId, String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;

        setTitle("Company Dashboard - " + companyName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // === MAIN PANEL ===
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(UIUtils.LIGHT_BG);

        // === HEADER ===
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(UIUtils.ACCENT);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblTitle = new JLabel("" + companyName + " Dashboard", SwingConstants.LEFT);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));

        JButton btnLogout = UIUtils.createButton("Logout");
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(UIUtils.ACCENT);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logged out successfully!");
            new ExistingCompanyForm().setVisible(true);
            dispose();
        });

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(btnLogout, BorderLayout.EAST);
        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // === CENTER GRID ===
        JPanel pnlCenter = new JPanel(new GridLayout(2, 2, 40, 40));
        pnlCenter.setBackground(UIUtils.LIGHT_BG);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(60, 120, 60, 120));

        JButton btnAccountingCycle = createDashboardButton("📘 Accounting Cycle", "Manage your accounting records and ledgers.");
        JButton btnTransactions = createDashboardButton("💰 Transactions", "Record and view all financial activities.");
        JButton btnReports = createDashboardButton("📊 Reports", "Generate financial reports and summaries.");
        JButton btnSettings = createDashboardButton("⚙ Settings", "Configure company preferences and options.");

        pnlCenter.add(btnAccountingCycle);
        pnlCenter.add(btnTransactions);
        pnlCenter.add(btnReports);
        pnlCenter.add(btnSettings);

        // === FOOTER ===
        JLabel lblFooter = new JLabel("© 2025 Accounting System | Developed by IT BOYS", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblFooter.setForeground(new Color(100, 100, 100));
        lblFooter.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        contentPane.add(pnlCenter, BorderLayout.CENTER);
        contentPane.add(lblFooter, BorderLayout.SOUTH);
        setContentPane(contentPane);

        // === BUTTON ACTIONS ===
        btnAccountingCycle.addActionListener(e -> {
            new AccountingCycleForm(companyId, companyName).setVisible(true);
            dispose();
        });

        btnTransactions.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Transaction module coming soon!", "Feature Info", JOptionPane.INFORMATION_MESSAGE)
        );

        btnReports.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Reports module coming soon!", "Feature Info", JOptionPane.INFORMATION_MESSAGE)
        );

        btnSettings.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Settings module coming soon!", "Feature Info", JOptionPane.INFORMATION_MESSAGE)
        );
    }

    private JButton createDashboardButton(String title, String subtitle) {
        JButton btn = new JButton("<html><center><b style='font-size:16px;'>" + title +
                "</b><br><span style='font-size:13px;color:gray;'>" + subtitle + "</span></center></html>");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 2),
                BorderFactory.createEmptyBorder(20, 10, 20, 10)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(UIUtils.ACCENT);
                btn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CompanyDashboard(1, "Sample Company").setVisible(true));
    }
}
