package UI;

import javax.swing.*;
import java.awt.*;

public class MainMenuForm extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MainMenuForm() {
        initComponents();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
    }

    private void initComponents() {
        // === Header ===
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(UIUtils.ACCENT);
        JLabel lblTitle = new JLabel("ACCOUNTING CYCLE SYSTEM", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        pnlHeader.add(lblTitle, BorderLayout.CENTER);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // === Body ===
        JPanel pnlBody = new JPanel();
        pnlBody.setBackground(UIUtils.LIGHT_BG);
        pnlBody.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(40, 40, 40, 40);

        // Cards
        JPanel cardAdd = UIUtils.createCard("Add Company", new ImageIcon("icons/add.png"));
        JPanel cardOpen = UIUtils.createCard("Open Existing", new ImageIcon("icons/open.jpg"));
        JPanel cardExit = UIUtils.createCard("Exit System", new ImageIcon("icons/exit.png"));

        // Click events
        cardAdd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new AddCompanyForm().setVisible(true);
                dispose();
            }
        });
        cardOpen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ExistingCompanyForm().setVisible(true);
                dispose();
            }
        });
        cardExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.exit(0);
            }
        });

        gbc.gridx = 0;
        pnlBody.add(cardAdd, gbc);
        gbc.gridx = 1;
        pnlBody.add(cardOpen, gbc);
        gbc.gridx = 2;
        pnlBody.add(cardExit, gbc);

        // === Footer ===
        JPanel pnlFooter = new JPanel();
        pnlFooter.setBackground(Color.WHITE);
        JLabel lblFooter = new JLabel("© 2025 Accounting Cycle System | Developed by IT BOYS", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblFooter.setForeground(new Color(100, 100, 100));
        pnlFooter.setLayout(new BorderLayout());
        pnlFooter.add(lblFooter, BorderLayout.CENTER);
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // === Frame Layout ===
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pnlHeader, BorderLayout.NORTH);
        getContentPane().add(pnlBody, BorderLayout.CENTER);
        getContentPane().add(pnlFooter, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Accounting Cycle System");
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuForm().setVisible(true));
    }
}
