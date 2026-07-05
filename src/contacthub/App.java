package contacthub;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.SQLException;

public class App 
{
    public static void main(String[] args) {
        // Use system look, then override with our dark theme
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        UITheme.applyDefaults();

        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);

            // Init DB in background
            SwingWorker<Void, String> worker = new SwingWorker<>() {
                boolean success = false;
                String errorMsg = "";

                @Override
                protected Void doInBackground() {
                    try {
                        publish("Connecting to MySQL...");
                        Thread.sleep(500);
                        publish("Initializing database...");
                        DatabaseConfig.initializeDatabase();
                        publish("Loading contacts...");
                        Thread.sleep(300);
                        success = true;
                    } catch (SQLException ex) {
                        errorMsg = ex.getMessage();
                    } catch (InterruptedException ignored) {}
                    return null;
                }

                @Override
                protected void process(java.util.List<String> chunks) {
                    splash.setStatus(chunks.get(chunks.size() - 1));
                }

                @Override
                protected void done() {
                    splash.dispose();
                    if (success) {
                        new MainWindow();
                    } else {
                        showConnectionError(errorMsg);
                    }
                }
            };
            worker.execute();
        });
    }

    private static void showConnectionError(String msg) 
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(new EmptyBorder(16, 16, 8, 16));

        JLabel title = new JLabel("⚠ Cannot Connect to MySQL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(UITheme.DANGER);

        JTextArea detail = new JTextArea(msg);
        detail.setFont(UITheme.FONT_SMALL);
        detail.setForeground(UITheme.TEXT_SECONDARY);
        detail.setBackground(UITheme.BG_INPUT);
        detail.setEditable(false);
        detail.setLineWrap(true);
        detail.setWrapStyleWord(true);
        detail.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel hint = new JLabel("<html><body style='color:#8B90AD'>" +
                "• Make sure MySQL is running<br>" +
                "• Check credentials in <b>DatabaseConfig.java</b><br>" +
                "• Ensure port 3306 is available" +
                "</body></html>");
        hint.setFont(UITheme.FONT_SMALL);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(detail) {{ setPreferredSize(new Dimension(400, 90)); }}, BorderLayout.CENTER);
        panel.add(hint, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(null, panel, "Connection Failed", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    // ─── Splash Screen ─────────────────────────────────────────────────────
    static class SplashScreen extends JWindow 
    {
        private JLabel statusLbl;
        private float  pulse = 0f;
        private final javax.swing.Timer anim;

        SplashScreen() 
        {
            setSize(480, 320);
            setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = UITheme.prep(g);
                    g2.setColor(UITheme.BG_DARK); // Use your new theme background
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    // Animated background glow
                    g2.setColor(new Color(123, 97, 255, 18 + (int)(8 * Math.abs(Math.sin(pulse)))));
                    g2.fillOval(-60, -60, 300, 300);

                    g2.setColor(UITheme.BORDER);
                    g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                    g2.dispose();
                }
            };
            root.setOpaque(false);

            // --- Logo Rendering with Image ---
            // --- Logo Rendering (Natural Shape) ---
            JPanel logo = new JPanel() {
                private final Image splashLogo = new ImageIcon("ContactHubImg.png").getImage();

                @Override 
                protected void paintComponent(Graphics g) 
                {
                    Graphics2D g2 = UITheme.prep(g);
                    int cx = getWidth() / 2, cy = getHeight() / 2;

                    // 1. Animated Background Glow (Still circular/soft for a premium feel)
                    int pr = (int)(10 + 6 * Math.abs(Math.sin(pulse)));
                    g2.setColor(new Color(123, 97, 255, 25));
                    g2.fillOval(cx - 40 - pr, cy - 40 - pr, (40 + pr) * 2, (40 + pr) * 2);

                    // 2. High-Quality Scaling
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    // Adjust these dimensions to match your logo's aspect ratio
                    int imgW = 100; 
                    int imgH = 100; 

                    // Draw the logo in its natural square/rectangular form
                    g2.drawImage(splashLogo, cx - imgW/2, cy - imgH/2, imgW, imgH, null);

                    g2.dispose();
                }

                @Override 
                public Dimension getPreferredSize() { return new Dimension(130, 130); }
            };
            logo.setOpaque(false);

            // Center Content
            JPanel center = new JPanel(new GridBagLayout());
            center.setOpaque(false);
            GridBagConstraints gc = new GridBagConstraints();
            gc.gridx = 0;

            gc.gridy = 0; gc.insets = new Insets(0, 0, 15, 0); 
            center.add(logo, gc);

            gc.gridy = 1; gc.insets = new Insets(0, 0, 5, 0);
            JLabel title = new JLabel("ContactHub"); // Updated Title
            title.setFont(new Font("Segoe UI", Font.BOLD, 32));
            title.setForeground(UITheme.TEXT_PRIMARY);
            center.add(title, gc);

            gc.gridy = 2; gc.insets = new Insets(0, 0, 30, 0);
            JLabel sub = new JLabel("Your Unified Space for Smart and Seamless Contact Management."); // Updated Subtitle
            sub.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            sub.setForeground(UITheme.TEXT_SECONDARY);
            center.add(sub, gc);

            gc.gridy = 3; gc.insets = new Insets(0, 0, 10, 0);
            statusLbl = new JLabel("Starting...");
            statusLbl.setFont(UITheme.FONT_SMALL);
            statusLbl.setForeground(UITheme.ACCENT);
            center.add(statusLbl, gc);

            gc.gridy = 4; gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(0, 60, 0, 60);
            JProgressBar bar = new JProgressBar();
            bar.setIndeterminate(true);
            bar.setBackground(UITheme.BG_INPUT);
            bar.setForeground(UITheme.ACCENT);
            bar.setPreferredSize(new Dimension(300, 4));
            center.add(bar, gc);

            root.add(center, BorderLayout.CENTER);

            JLabel ver = new JLabel("v3.0  ·  Java + MySQL"); // Updated Version info
            ver.setFont(UITheme.FONT_SMALL);
            ver.setForeground(UITheme.TEXT_MUTED);
            ver.setBorder(new EmptyBorder(0, 0, 15, 20));
            ver.setHorizontalAlignment(SwingConstants.RIGHT);
            root.add(ver, BorderLayout.SOUTH);

            setContentPane(root);
            anim = new javax.swing.Timer(40, e -> { pulse += 0.06f; root.repaint(); logo.repaint(); });
            anim.start();
        }

        void setStatus(String s) { statusLbl.setText(s); }
        @Override public void dispose() { anim.stop(); super.dispose(); }
    }
}
