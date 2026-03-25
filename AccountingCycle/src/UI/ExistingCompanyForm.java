package UI;

import DAO.CompanyDAO;
import Model.Company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class ExistingCompanyForm extends JFrame {
    private JTable tblCompanies;
    private JButton btnOpen, btnDelete, btnReload, btnBack;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilter;
    private List<Company> allCompanies;

    public ExistingCompanyForm() {
        initComponents();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
    }

    private void initComponents() {
        // === Header ===
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(UIUtils.ACCENT);
        JLabel lblTitle = new JLabel("📂 Existing Companies", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 34));
        pnlHeader.add(lblTitle, BorderLayout.CENTER);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        // === Search & Filter ===
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        pnlSearch.setBackground(UIUtils.LIGHT_BG);

        JLabel lblSearch = new JLabel("🔍 Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JLabel lblFilter = new JLabel("Filter:");
        lblFilter.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        cmbFilter = new JComboBox<>(new String[]{"All", "Sort by Name (A-Z)", "Sort by ID"});
        cmbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        pnlSearch.add(lblSearch);
        pnlSearch.add(txtSearch);
        pnlSearch.add(lblFilter);
        pnlSearch.add(cmbFilter);

        // === Table ===
        tblCompanies = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"Company ID", "Company Name"}
        ));
        tblCompanies.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        tblCompanies.setRowHeight(40);
        tblCompanies.setSelectionBackground(UIUtils.ACCENT);
        tblCompanies.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tblCompanies);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // === Buttons ===
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        pnlButtons.setBackground(UIUtils.LIGHT_BG);

        btnOpen = UIUtils.createButton("Open Company");
        btnDelete = UIUtils.createButton("Delete Company");
        btnReload = UIUtils.createButton("Reload List");
        btnBack = UIUtils.createButton("Back to Menu");

        pnlButtons.add(btnOpen);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnReload);
        pnlButtons.add(btnBack);

        pnlButtons.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // === Footer ===
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(Color.WHITE);
        JLabel lblFooter = new JLabel("© 2025 Accounting Cycle System | Developed by IT BOYS", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblFooter.setForeground(new Color(100, 100, 100));
        pnlFooter.add(lblFooter, BorderLayout.CENTER);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        // === Combine Buttons + Footer ===
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.add(pnlButtons, BorderLayout.CENTER);
        pnlBottom.add(pnlFooter, BorderLayout.SOUTH);

        // === Load Data ===
        loadCompanyTable();

        // === Event Listeners ===
        btnReload.addActionListener(e -> loadCompanyTable());
        btnBack.addActionListener(e -> {
            new MainMenuForm().setVisible(true);
            dispose();
        });
        btnDelete.addActionListener(e -> deleteCompany());
        btnOpen.addActionListener(e -> openCompany());

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterCompanyList();
            }
        });

        cmbFilter.addActionListener(e -> filterCompanyList());

        // === Frame Layout ===
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pnlHeader, BorderLayout.NORTH);
        getContentPane().add(pnlSearch, BorderLayout.BEFORE_FIRST_LINE);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(pnlBottom, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Existing Companies");
        pack();
    }

    // ====================== LOGIC ======================
    private void loadCompanyTable() {
        allCompanies = CompanyDAO.getAllCompanies();
        populateTable(allCompanies);
    }

    private void populateTable(List<Company> companies) {
        DefaultTableModel model = (DefaultTableModel) tblCompanies.getModel();
        model.setRowCount(0);
        for (Company c : companies) {
            model.addRow(new Object[]{c.getCompanyId(), c.getCompanyName()});
        }
    }

    private void filterCompanyList() {
        String search = txtSearch.getText().toLowerCase().trim();
        String filter = (String) cmbFilter.getSelectedItem();

        List<Company> filtered = allCompanies.stream()
                .filter(c -> c.getCompanyName().toLowerCase().contains(search))
                .collect(Collectors.toList());

        if ("Sort by Name (A-Z)".equals(filter)) {
            filtered.sort((a, b) -> a.getCompanyName().compareToIgnoreCase(b.getCompanyName()));
        } else if ("Sort by ID".equals(filter)) {
            filtered.sort((a, b) -> Integer.compare(a.getCompanyId(), b.getCompanyId()));
        }

        populateTable(filtered);
    }

    private void deleteCompany() {
        int row = tblCompanies.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a company first.");
            return;
        }

        int companyId = (int) tblCompanies.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this company?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = CompanyDAO.deleteCompany(companyId);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "✅ Company deleted successfully!");
                loadCompanyTable();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to delete company.");
            }
        }
    }

    private void openCompany() {
        int row = tblCompanies.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a company first.");
            return;
        }

        int companyId = (int) tblCompanies.getValueAt(row, 0);
        String companyName = (String) tblCompanies.getValueAt(row, 1);

        // Open the company dashboard
        new CompanyDashboard(companyId, companyName).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExistingCompanyForm().setVisible(true));
    }
}
