package UI;



import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Set;
import DAO.GeneralLedgerDAO;
import DAO.JournalEntryDAO;
import Model.GeneralLedger;
import Model.JournalEntry;

public class GeneralLedgerForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private int companyId;
    private String companyName;

    public GeneralLedgerForm(int companyId, String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;
        initUI();
    }

    private void initUI() {
        setTitle("General Ledger - " + companyName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        add(UIStyle.createHeader("General Ledger", companyName), BorderLayout.NORTH);

        // ===== BODY =====
        JPanel pnlBody = new JPanel();
        pnlBody.setBackground(UIStyle.BG_COLOR);
        pnlBody.setLayout(new BoxLayout(pnlBody, BoxLayout.Y_AXIS));
        pnlBody.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // 🔹 Get all accounts from journal entries
        Set<String> accountNames = loadAllAccounts();

        // 🔹 Load general ledger data grouped by account
        Map<String, List<GeneralLedger>> ledgerMap = loadLedgerData();

        if (accountNames.isEmpty()) {
            JLabel lblEmpty = new JLabel("No journal entries found.", SwingConstants.CENTER);
            lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            lblEmpty.setForeground(Color.GRAY);
            pnlBody.add(lblEmpty);
        } else {
            for (String account : accountNames) {
                List<GeneralLedger> entries = ledgerMap.getOrDefault(account, new ArrayList<>());
                pnlBody.add(createLedgerPanel(account, entries));
                pnlBody.add(Box.createVerticalStrut(25)); // spacing between panels
            }
        }

        JScrollPane scrollPane = new JScrollPane(pnlBody);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JButton btnBack = new JButton("Back to Cycle");
        UIStyle.styleButton(btnBack);
        pnlFooter.add(btnBack, BorderLayout.WEST);

        JLabel lblFooter = new JLabel("© 2025 Accounting Cycle System | Developed by IT BOYS", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblFooter.setForeground(new Color(100, 100, 100));
        pnlFooter.add(lblFooter, BorderLayout.SOUTH);

        add(pnlFooter, BorderLayout.SOUTH);

        // ===== ACTION =====
        btnBack.addActionListener(e -> {
            new AccountingCycleForm(companyId, companyName).setVisible(true);
            dispose();
        });
    }

    /**
     * 🔹 Load all account names (debit + credit) from journal_entry for the given company
     */
    private Set<String> loadAllAccounts() {
        Set<String> accounts = new LinkedHashSet<>();
        List<JournalEntry> entries = JournalEntryDAO.getEntriesByCompany(companyId);

        for (JournalEntry je : entries) {
            if (je.getDebitAccount() != null && !je.getDebitAccount().isBlank()) {
                accounts.add(je.getDebitAccount());
            }
            if (je.getCreditAccount() != null && !je.getCreditAccount().isBlank()) {
                accounts.add(je.getCreditAccount());
            }
        }

        return accounts;
    }

    /**
     * 🔹 Load general ledger entries grouped by account
     */
    private Map<String, List<GeneralLedger>> loadLedgerData() {
        List<GeneralLedger> allEntries = GeneralLedgerDAO.getLedgerByCompany(companyId);
        Map<String, List<GeneralLedger>> map = new LinkedHashMap<>();

        for (GeneralLedger gl : allEntries) {
            if (gl.getAccountName() != null && !gl.getAccountName().isBlank()) {
                map.computeIfAbsent(gl.getAccountName(), k -> new ArrayList<>()).add(gl);
            }
        }

        return map;
    }

    /**
     * 🔹 Create a ledger panel showing all transactions for one account
     */
    private JPanel createLedgerPanel(String accountName, List<GeneralLedger> entries) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // ===== TITLE =====
        JLabel lblTitle = new JLabel(accountName, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(UIStyle.ACCENT);
        panel.add(lblTitle, BorderLayout.NORTH);

        // ===== TABLE =====
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Date", "Debit", "Credit", "Balance"}, 0);
        double totalDebit = 0;
        double totalCredit = 0;

        if (entries.isEmpty()) {
            model.addRow(new Object[]{"—", "₱0.00", "₱0.00", "₱0.00"});
        } else {
            for (GeneralLedger gl : entries) {
                String date = (gl.getTransactionDate() != null)
                        ? gl.getTransactionDate().toString()
                        : "—";

                model.addRow(new Object[]{
                        date,
                        String.format("₱%.2f", gl.getDebit()),
                        String.format("₱%.2f", gl.getCredit()),
                        String.format("₱%.2f", gl.getBalance())
                });
                totalDebit += gl.getDebit();
                totalCredit += gl.getCredit();
            }
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ===== TOTAL LABEL =====
        JLabel lblTotal = new JLabel(
                String.format("Total Debit: ₱%.2f   |   Total Credit: ₱%.2f", totalDebit, totalCredit),
                SwingConstants.CENTER);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(new Color(90, 90, 90));
        panel.add(lblTotal, BorderLayout.SOUTH);

        return panel;
    }
}
