package contacthub;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

public class UITheme {
    // --- Colors ---
    public static final Color BG_DARK      = new Color(0x070B14);
    public static final Color BG_CARD      = new Color(0x0D121D);
    public static final Color BG_INPUT     = new Color(0x0F1624);
    public static final Color BORDER       = new Color(0x1F2937);
    public static final Color ACCENT       = new Color(0x7B61FF);
    public static final Color ACCENT_GLOW  = new Color(0xA394FF);
    public static final Color TEXT_PRIMARY = new Color(0xE5E7EB);
    public static final Color TEXT_SECONDARY = new Color(0x9CA3AF);
    public static final Color TEXT_MUTED     = new Color(0x4B5563);
    public static final Color ROW_SELECT     = new Color(0x1E2530);
    public static final Color SUCCESS        = new Color(0x34D399);
    public static final Color WARN           = new Color(0xFBBF24);
    public static final Color DANGER         = new Color(0xFF5470);

    // --- Fonts ---
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY  = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    public static void applyDefaults() {
        UIManager.put("Panel.background", BG_DARK);
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
    }

    public static JTextField styledField(String hint) {
        JTextField f = new JTextField(hint);
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_MUTED);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true), new EmptyBorder(0, 15, 0, 15)));

        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (f.getText().equals(hint)) { f.setText(""); f.setForeground(TEXT_PRIMARY); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setForeground(TEXT_MUTED); f.setText(hint); }
            }
        });
        return f;
    }

    public static JTextArea styledArea(int rows) {
        JTextArea a = new JTextArea(rows, 20);
        a.setBackground(BG_INPUT);
        a.setForeground(TEXT_PRIMARY);
        a.setCaretColor(ACCENT);
        a.setFont(FONT_BODY);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(new EmptyBorder(10, 14, 10, 14));
        return a;
    }

    public static JComboBox<String> styledCombo(String... items) {
        JComboBox<String> b = new JComboBox<>(items);
        b.setBackground(BG_INPUT);
        b.setForeground(TEXT_PRIMARY);
        b.setFont(FONT_BODY);
        b.setBorder(new LineBorder(BORDER, 1));
        return b;
    }

    // --- High Quality Circular Icon ---
    public static class CircleIcon implements Icon {
        private final BufferedImage image;
        private final int size;

        public CircleIcon(Image img, int size) {
            this.size = size;
            this.image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = this.image.createGraphics();
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
            g2.drawImage(img, 0, 0, size, size, null);
            
            g2.setClip(null);
            g2.setColor(new Color(0x242E42));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawOval(0, 0, size - 1, size - 1);
            g2.dispose();
        }

        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(image, x, y, null);
            g2.dispose();
        }
        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    // --- High-End Header Renderer ---
    public static class HeaderRenderer extends DefaultTableCellRenderer 
    {
        private static final Color HEADER_BG = new Color(0x161D2B);
        private static final Color HEADER_TEXT = new Color(0xA3B1C6);

        public HeaderRenderer() {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            super.getTableCellRendererComponent(t, v, s, f, r, c);
            setBackground(HEADER_BG);
            setForeground(HEADER_TEXT);
            setBorder(BorderFactory.createCompoundBorder( BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER), BorderFactory.createEmptyBorder(12, 18, 12, 15)));
            return this;
        }
    }

    public static class GradientPanel extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(BG_CARD);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    public static class StyledButton extends JButton {
        public StyledButton(String text, Color bg, Color hover) {
            super(text);
            setBackground(bg);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(FONT_BODY);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    public static class TableRenderer extends DefaultTableCellRenderer {
    @Override 
    public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
        super.getTableCellRendererComponent(t, v, s, f, r, c);
        setBackground(s ? ROW_SELECT : BG_DARK);
        setForeground(s ? Color.WHITE : TEXT_PRIMARY);
        
        // Remove the default EmptyBorder that was 18px wide 
        // and replace it with a simple left-padding only.
        // This ensures the right and bottom grid lines stay visible.
        setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0)); 
        
        return this;
    }
}

    public static class BadgeRenderer extends TableRenderer {}

    public static class RoundedBorder implements Border {
        private int radius; Color color;
        public RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        public Insets getBorderInsets(Component c) { return new Insets(1, 1, 1, 1); }
        public boolean isBorderOpaque() { return true; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
    
    public static Graphics2D prep(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    return g2;
}
}
