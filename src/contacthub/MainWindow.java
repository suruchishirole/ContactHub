package contacthub;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainWindow extends JFrame {

    private ContactDAO dao;
    private DefaultTableModel tableModel;
    private JTable contactTable;
    private JTextField searchField;
    private JLabel totalLabel;
    private JLabel statusLabel;

    private JLabel statTotal, statFamily, statWork, statVIP;

    private static final String[] COLUMNS = {"ID", "Name", "Phone", "Email", "Category", "Added"};
    private static final int[] COL_WIDTHS = {50, 200, 150, 220, 120, 160};

    public MainWindow() {
        dao = new ContactDAO();
        buildUI();
        loadContacts(null);
        setVisible(true);
    }

    private void buildUI() {
        setTitle("ContactHub Application - Smart Contact Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                DatabaseConfig.closeConnection();
                System.exit(0);
            }
        });
        setSize(1280, 820);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        UITheme.GradientPanel header = new UITheme.GradientPanel();
        header.setLayout(new BorderLayout(40, 0));
        header.setBorder(new EmptyBorder(15, 28, 10, 28));
        header.setPreferredSize(new Dimension(0, 100)); 

        JPanel brandPanel = new JPanel(new BorderLayout(18, 0)); 
        brandPanel.setOpaque(false);

        // Circular Logo
        ImageIcon rawIcon = new ImageIcon("ContactHubLogo.png"); 
        UITheme.CircleIcon circleIcon = new UITheme.CircleIcon(rawIcon.getImage(), 60);
        JLabel iconLbl = new JLabel(circleIcon);
        
        JPanel titleStack = new JPanel(new GridLayout(2, 1, 0, -6)); 
        titleStack.setOpaque(false);
        
        JLabel appTitle = new JLabel("ContactHub");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 28)); 
        appTitle.setForeground(UITheme.TEXT_PRIMARY);
        
        JLabel appSub = new JLabel("Your Unified Space for Smart and Seamless Contact Management."); 
        appSub.setFont(new Font("Segoe UI", Font.ITALIC, 15)); 
        appSub.setForeground(UITheme.TEXT_SECONDARY);
        
        titleStack.add(appTitle);
        titleStack.add(appSub);

        brandPanel.add(iconLbl, BorderLayout.WEST);
        brandPanel.add(titleStack, BorderLayout.CENTER);
        header.add(brandPanel, BorderLayout.WEST);

        JPanel searchContainer = new JPanel(new GridBagLayout());
        searchContainer.setOpaque(false);
        searchField = UITheme.styledField("Search by name, phone, or email...");
        searchField.setPreferredSize(new Dimension(420, 44)); 
        
        Timer[] debounce = {null};
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { debounce(debounce); }
            @Override public void removeUpdate(DocumentEvent e)  { debounce(debounce); }
            @Override public void changedUpdate(DocumentEvent e) { debounce(debounce); }
        });
        searchContainer.add(searchField);
        header.add(searchContainer, BorderLayout.CENTER);

        JPanel btnContainer = new JPanel(new GridBagLayout());
        btnContainer.setOpaque(false);
        UITheme.StyledButton addBtn = new UITheme.StyledButton("＋ Add Contact", UITheme.ACCENT, UITheme.ACCENT_GLOW);
        addBtn.setPreferredSize(new Dimension(160, 44));
        addBtn.addActionListener(e -> onAddContact());
        btnContainer.add(addBtn);
        header.add(btnContainer, BorderLayout.EAST);

        return header;
    }

    private void debounce(Timer[] holder) {
        if (holder[0] != null) holder[0].cancel();
        holder[0] = new Timer();
        holder[0].schedule(new TimerTask() {
            @Override public void run() {
                SwingUtilities.invokeLater(() -> {
                    String q = searchField.getText().trim();
                    if(q.equals("Search by name, phone, or email...")) q = "";
                    loadContacts(q.isEmpty() ? null : q);
                });
            }
        }, 300);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_CARD);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER));

        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(sideLabel("OVERVIEW", true));
        sidebar.add(Box.createVerticalStrut(20));

        statTotal  = statRow(sidebar, "Total Contacts", "0", UITheme.ACCENT_GLOW);
        statFamily = statRow(sidebar, "Family", "0", UITheme.SUCCESS);
        statWork   = statRow(sidebar, "Work", "0", UITheme.WARN);
        statVIP    = statRow(sidebar, "VIP", "0", new Color(0xFF6B9D));

        sidebar.add(Box.createVerticalStrut(24));
        sidebar.add(new JSeparator() {{ setForeground(UITheme.BORDER); setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); }});
        sidebar.add(Box.createVerticalStrut(20));

        sidebar.add(sideLabel("FILTER BY CATEGORY", true));
        String[] cats = {"All Contacts", "General", "Family", "Friends", "Work", "VIP"};
        for (String cat : cats) { sidebar.add(filterButton(cat)); sidebar.add(Box.createVerticalStrut(4)); }

        sidebar.add(Box.createVerticalGlue());
        
        UITheme.StyledButton editBtn = new UITheme.StyledButton("✏️ Edit Selected", new Color(0x2A2F50), UITheme.BG_INPUT);
        editBtn.setMaximumSize(new Dimension(210, 42)); editBtn.setAlignmentX(CENTER_ALIGNMENT);
        editBtn.addActionListener(e -> onEditContact());
        
        UITheme.StyledButton delBtn = new UITheme.StyledButton("🗑️ Delete Selected", new Color(0x3A1020), UITheme.DANGER);
        delBtn.setMaximumSize(new Dimension(210, 42)); delBtn.setAlignmentX(CENTER_ALIGNMENT);
        delBtn.addActionListener(e -> onDeleteContact());

        sidebar.add(editBtn); sidebar.add(Box.createVerticalStrut(10)); sidebar.add(delBtn);
        sidebar.add(Box.createVerticalStrut(25));
        return sidebar;
    }

        private JLabel sideLabel(String text, boolean header) {
    // Remove the leading spaces in the text
    JLabel lbl = new JLabel(text); 
    lbl.setFont(header ? new Font("Segoe UI", Font.BOLD, 12) : UITheme.FONT_BODY);
    lbl.setForeground(header ? UITheme.TEXT_MUTED : UITheme.TEXT_SECONDARY);
    
    // Align the label component to the left
    lbl.setAlignmentX(LEFT_ALIGNMENT);
    
    // Give it a small 5px left margin to match the buttons
    lbl.setBorder(new EmptyBorder(0, 5, 0, 0)); 
    
    return lbl;
}

        private JLabel statRow(JPanel parent, String label, String value, Color color) {
    JPanel row = new JPanel(new BorderLayout());
    row.setOpaque(false);
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42)); // Increased height from 36
    row.setBorder(new EmptyBorder(2, 24, 2, 24));

    JLabel labelLbl = new JLabel(label);
    // Increased font for the stat labels
    labelLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13)); 
    labelLbl.setForeground(UITheme.TEXT_SECONDARY);

    JLabel valueLbl = new JLabel(value);
    // Increased font for the numbers (the values)
    valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
    valueLbl.setForeground(color);

    row.add(labelLbl, BorderLayout.WEST);
    row.add(valueLbl, BorderLayout.EAST);
    parent.add(row);
    return valueLbl;
}

        private JButton filterButton(String label) {
    JButton btn = new JButton("- " + label) {
        boolean hover = false;
        { addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited (MouseEvent e) { hover = false; repaint(); }
        });}
        @Override protected void paintComponent(Graphics g) {
            if (hover) { g.setColor(UITheme.BG_INPUT); g.fillRect(0, 0, getWidth(), getHeight()); }
            super.paintComponent(g);
        }
    };

    btn.setFont(UITheme.FONT_BODY);
    btn.setForeground(UITheme.TEXT_SECONDARY);
    
    // Force text to the left
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    btn.setOpaque(false);
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    // Remove the large left padding (Changed from 24/12 to 5 for a tiny margin)
    btn.setBorder(new EmptyBorder(4, 5, 4, 5)); 
    
    // Ensure the button itself stays left-aligned in the sidebar
    btn.setAlignmentX(LEFT_ALIGNMENT);
    btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

    btn.addActionListener(e -> {
        searchField.setText("");
        if (label.equals("All Contacts")) loadContacts(null);
        else loadByCategory(label);
    });
    return btn;
}

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UITheme.BG_DARK); content.setBorder(new EmptyBorder(16, 20, 0, 20));

        tableModel = new DefaultTableModel(COLUMNS, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        contactTable = new JTable(tableModel);
        contactTable.setBackground(UITheme.BG_CARD);
contactTable.setRowHeight(52);
contactTable.setSelectionBackground(UITheme.ROW_SELECT);

        contactTable.setShowGrid(true); // Enable lines
contactTable.setGridColor(new Color(0x2D3748)); // Subtle slate-grey
contactTable.setShowVerticalLines(true);
contactTable.setShowHorizontalLines(true);

// Adjust spacing so lines are visible
contactTable.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = contactTable.getTableHeader();
        header.setDefaultRenderer(new UITheme.HeaderRenderer());
        header.setPreferredSize(new Dimension(0, 52));

        UITheme.TableRenderer tr = new UITheme.TableRenderer();
        UITheme.BadgeRenderer br = new UITheme.BadgeRenderer();
        for (int i = 0; i < COLUMNS.length; i++) {
            TableColumn col = contactTable.getColumnModel().getColumn(i);
            col.setPreferredWidth(COL_WIDTHS[i]);
            col.setCellRenderer(i == 4 ? br : tr);
        }

        JScrollPane scroll = new JScrollPane(contactTable);
        scroll.setBorder(new UITheme.RoundedBorder(14, UITheme.BORDER));
        scroll.getViewport().setBackground(UITheme.BG_CARD);
        content.add(scroll, BorderLayout.CENTER);

        totalLabel = new JLabel("0 contacts");
        totalLabel.setFont(UITheme.FONT_SMALL); totalLabel.setForeground(UITheme.TEXT_MUTED);
        JPanel info = new JPanel(new BorderLayout()); info.setOpaque(false); info.setBorder(new EmptyBorder(12, 5, 12, 5));
        info.add(totalLabel, BorderLayout.WEST);
        content.add(info, BorderLayout.SOUTH);

        return content;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_CARD); bar.setBorder(new EmptyBorder(8, 24, 8, 24));
        statusLabel = new JLabel("Ready"); statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.SUCCESS); bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private void loadContacts(String q) {
        try { List<Contact> l = (q == null) ? dao.getAllContacts() : dao.searchContacts(q); populateTable(l); updateStats(); } catch (SQLException ex) {}
    }

    private void loadByCategory(String cat) {
        try { populateTable(dao.getByCategory(cat)); } catch (SQLException ex) {}
    }

    private void populateTable(List<Contact> list) {
        tableModel.setRowCount(0);
        for (Contact c : list) tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getEmail() != null ? c.getEmail() : "—", c.getCategory(), c.getCreatedAtFormatted()});
        totalLabel.setText(list.size() + " contact(s) displayed");
    }

    private void updateStats() {
        try {
            statTotal.setText(String.valueOf(dao.getTotalCount()));
            statFamily.setText(String.valueOf(dao.getByCategory("Family").size()));
            statWork.setText(String.valueOf(dao.getByCategory("Work").size()));
            statVIP.setText(String.valueOf(dao.getByCategory("VIP").size()));
        } catch (SQLException ignored) {}
    }

    private void onAddContact() {
        ContactDialog dlg = new ContactDialog(this, "Add Contact", null);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) { try { dao.addContact(dlg.getContact()); loadContacts(null); } catch (SQLException ex) {} }
    }

    private void onEditContact() {
        int row = contactTable.getSelectedRow();
        if (row < 0) return;
        Contact c = new Contact();
        c.setId((int) tableModel.getValueAt(row, 0));
        c.setName((String) tableModel.getValueAt(row, 1));
        c.setPhone((String) tableModel.getValueAt(row, 2));
        c.setCategory((String) tableModel.getValueAt(row, 4));
        ContactDialog dlg = new ContactDialog(this, "Edit Contact", c);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) { try { dao.updateContact(dlg.getContact()); loadContacts(null); } catch (SQLException ex) {} }
    }

    private void onDeleteContact() {
        int row = contactTable.getSelectedRow();
        if (row < 0) return;
        if (JOptionPane.showConfirmDialog(this, "Delete contact?", "Confirm", 0) == 0) {
            try { dao.deleteContact((int) tableModel.getValueAt(row, 0)); loadContacts(null); } catch (SQLException ex) {}
        }
    }
}
