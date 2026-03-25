package UI;

import DAO.CompanyDAO;
import javax.swing.*;
import java.awt.*;

public class AddCompanyForm extends JFrame {
    private JTextField txtCompanyName;
    private JButton btnSave, btnBack;

    public AddCompanyForm() {
        initComponents();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
    }

    private void initComponents() {
        // === Header ===
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(UIUtils.ACCENT);
        JLabel lblTitle = new JLabel("Add New Company", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        pnlHeader.add(lblTitle, BorderLayout.CENTER);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // === Body ===
        JPanel pnlBody = new JPanel();
        pnlBody.setBackground(UIUtils.LIGHT_BG);
        pnlBody.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCompanyName = new JLabel("Company Name:");
        lblCompanyName.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtCompanyName = new JTextField(25);
        txtCompanyName.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtCompanyName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        btnSave = UIUtils.createButton("Save Company");
        btnBack = UIUtils.createButton("Back to Menu");

        gbc.gridx = 0; gbc.gridy = 0;
        pnlBody.add(lblCompanyName, gbc);
        gbc.gridx = 1;
        pnlBody.add(txtCompanyName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        pnlBody.add(btnSave, gbc);

        gbc.gridy = 2;
        pnlBody.add(btnBack, gbc);

        // === Footer ===
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(Color.WHITE);
        JLabel lblFooter = new JLabel("© 2025 Accounting Cycle System | Developed by IT BOYS", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblFooter.setForeground(new Color(100, 100, 100));
        pnlFooter.add(lblFooter, BorderLayout.CENTER);
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // === Events ===
        btnSave.addActionListener(e -> saveCompany());
        btnBack.addActionListener(e -> {
            new MainMenuForm().setVisible(true);
            dispose();
        });

        // === Frame Layout ===
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pnlHeader, BorderLayout.NORTH);
        getContentPane().add(pnlBody, BorderLayout.CENTER);
        getContentPane().add(pnlFooter, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Add Company");
        pack();
    }

    private void saveCompany() {
        String companyName = txtCompanyName.getText().trim();
        if (companyName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a company name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = CompanyDAO.addCompany(companyName);
        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Company '" + companyName + "' added successfully!");
            txtCompanyName.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to add company.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddCompanyForm().setVisible(true));
    }
}
