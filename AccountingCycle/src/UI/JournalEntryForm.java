package UI;

import javax.swing.*;
import javax.swing.table.*;
import DAO.JournalEntryDAO;
import Model.JournalEntry;
import java.awt.*;
import java.util.List;
import java.util.Collections;

public class JournalEntryForm extends JFrame {

    private int companyId;
    private String companyName;
    private DefaultTableModel model;
    private JTextField txtDate, txtDebitAccount, txtCreditAccount, txtDesc, txtDebitAmt, txtCreditAmt;

    public JournalEntryForm(int companyId, String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;
        initUI();
        loadJournalEntries();
    }

    private void initUI() {
        setTitle("Journal Entries - " + companyName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // HEADER
        JLabel headerLabel = new JLabel("📘 Journal Entries", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // FORM PANEL
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtDate = new JTextField(15);
        txtDesc = new JTextField(20);
        txtDebitAccount = new JTextField(15);
        txtCreditAccount = new JTextField(15);
        txtDebitAmt = new JTextField(10);
        txtCreditAmt = new JTextField(10);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; formPanel.add(txtDate, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 3; formPanel.add(txtDesc, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Debit Account:"), gbc);
        gbc.gridx = 1; formPanel.add(txtDebitAccount, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Credit Account:"), gbc);
        gbc.gridx = 3; formPanel.add(txtCreditAccount, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Debit Amount:"), gbc);
        gbc.gridx = 1; formPanel.add(txtDebitAmt, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Credit Amount:"), gbc);
        gbc.gridx = 3; formPanel.add(txtCreditAmt, gbc);

        add(formPanel, BorderLayout.NORTH);

        // TABLE
        model = new DefaultTableModel(new Object[]{
                "ID", "Date", "Account", "Description", "Debit", "Credit"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Custom renderer for alternating rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                if (row % 2 == 1) { // credit row
                    if (col == 2) {
                        setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0)); // indent credit account
                    } else {
                        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                    }
                    c.setBackground(new Color(248, 248, 248));
                } else {
                    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        add(scrollPane, BorderLayout.CENTER);

        // FOOTER PANEL
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnAdd = new JButton("Add Entry");
        JButton btnClear = new JButton("Clear Fields");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnBack = new JButton("Back");

        styleButton(btnAdd);
        styleButton(btnClear);
        styleButton(btnDelete);
        styleButton(btnBack);

        footer.add(btnAdd);
        footer.add(btnClear);
        footer.add(btnDelete);
        footer.add(btnBack);

        add(footer, BorderLayout.SOUTH);

        // ACTIONS
        btnAdd.addActionListener(e -> addJournalEntry());
        btnClear.addActionListener(e -> clearFields());
        btnDelete.addActionListener(e -> deleteSelectedEntry(table));

        // ✅ FIXED: Proper back navigation
        btnBack.addActionListener(e -> {
            new AccountingCycleForm(companyId, companyName).setVisible(true);
            dispose();
        });
    }

    private void loadJournalEntries() {
        model.setRowCount(0);
        List<JournalEntry> entries = JournalEntryDAO.getEntriesByCompany(companyId);

        // ✅ Reverse the list to show oldest entries first (ascending order)
        Collections.reverse(entries);

        for (JournalEntry je : entries) {
            model.addRow(new Object[]{
                    je.getJournalId(),
                    je.getEntryDate(),
                    je.getDebitAccount(),
                    je.getDescription(),
                    String.format("%.2f", je.getDebitAmount()),
                    ""
            });

            model.addRow(new Object[]{
                    je.getJournalId(),
                    "",
                    "   ↳ " + je.getCreditAccount(),
                    "",
                    "",
                    String.format("%.2f", je.getCreditAmount())
            });
        }
    }

    private void addJournalEntry() {
        try {
            if (txtDate.getText().isEmpty() || txtDebitAccount.getText().isEmpty() ||
                txtCreditAccount.getText().isEmpty() || txtDesc.getText().isEmpty() ||
                txtDebitAmt.getText().isEmpty() || txtCreditAmt.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double debit = Double.parseDouble(txtDebitAmt.getText());
            double credit = Double.parseDouble(txtCreditAmt.getText());

            if (debit != credit) {
                JOptionPane.showMessageDialog(this, "Debit and Credit amounts must be equal!", "Balance Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ✅ Save main journal entry
            JournalEntry mainEntry = new JournalEntry();
            mainEntry.setCompanyId(companyId);
            mainEntry.setEntryDate(txtDate.getText());
            mainEntry.setDescription(txtDesc.getText());
            mainEntry.setDebitAccount(txtDebitAccount.getText());
            mainEntry.setCreditAccount(txtCreditAccount.getText());
            mainEntry.setDebitAmount(debit);
            mainEntry.setCreditAmount(credit);

            boolean success = JournalEntryDAO.addJournalEntry(mainEntry);

            // ✅ If the credit account is Sales, add Freight Out + COGS entries
            if (txtCreditAccount.getText().equalsIgnoreCase("Sales")) {
                String freightOutText = JOptionPane.showInputDialog(this, "Enter Freight Out amount (₱):", "0");
                String cogsText = JOptionPane.showInputDialog(this, "Enter Cost of Goods Sold (₱):", "0");

                double freightOut = Double.parseDouble(freightOutText);
                double cogs = Double.parseDouble(cogsText);

                if (cogs > 0) {
                    JournalEntry cogsEntry = new JournalEntry();
                    cogsEntry.setCompanyId(companyId);
                    cogsEntry.setEntryDate(txtDate.getText());
                    cogsEntry.setDescription("Record cost of goods sold");
                    cogsEntry.setDebitAccount("Cost of Goods Sold");
                    cogsEntry.setCreditAccount("Merchandise Inventory");
                    cogsEntry.setDebitAmount(cogs);
                    cogsEntry.setCreditAmount(cogs);
                    JournalEntryDAO.addJournalEntry(cogsEntry);
                }

                if (freightOut > 0) {
                    JournalEntry freightEntry = new JournalEntry();
                    freightEntry.setCompanyId(companyId);
                    freightEntry.setEntryDate(txtDate.getText());
                    freightEntry.setDescription("Record freight out expense");
                    freightEntry.setDebitAccount("Freight Out");
                    freightEntry.setCreditAccount("Cash");
                    freightEntry.setDebitAmount(freightOut);
                    freightEntry.setCreditAmount(freightOut);
                    JournalEntryDAO.addJournalEntry(freightEntry);
                }
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Journal entry added successfully!");
                loadJournalEntries();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error saving journal entry.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void deleteSelectedEntry(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) model.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this journal entry?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (JournalEntryDAO.deleteJournalEntry(id)) {
                    JOptionPane.showMessageDialog(this, "Entry deleted.");
                    loadJournalEntries();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete entry.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete.");
        }
    }

    private void clearFields() {
        txtDate.setText("");
        txtDebitAccount.setText("");
        txtCreditAccount.setText("");
        txtDesc.setText("");
        txtDebitAmt.setText("");
        txtCreditAmt.setText("");
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0x2E86C1));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}