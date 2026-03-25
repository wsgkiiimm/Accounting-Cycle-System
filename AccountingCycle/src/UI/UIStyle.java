package UI;

import java.awt.*;
import javax.swing.*;

public class UIStyle {

    public static final Color PRIMARY_COLOR = new Color(25, 42, 86);
    public static final Color BG_COLOR = new Color(245, 245, 245);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
	protected static final Color ACCENT = null;

    public static JPanel createHeader(String title, String companyName) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(100, 70));

        JLabel lbl = new JLabel(companyName + " | " + title, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(HEADER_FONT);
        header.add(lbl, BorderLayout.CENTER);

        return header;
    }

    public static JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(PRIMARY_COLOR);
        JLabel lbl = new JLabel("© 2025 Accounting System | Developed by IT BOYS");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        footer.add(lbl);
        return footer;
    }

    public static void styleButton(JButton btn) {
        btn.setBackground(new Color(41, 128, 185));
        btn.setForeground(Color.WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
    }
}
