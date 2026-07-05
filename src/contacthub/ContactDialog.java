package contacthub;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ContactDialog extends JDialog {

    private JTextField nameField, phoneField, emailField;
    private JComboBox<String> categoryBox;
    private JTextArea notesArea;
    private boolean confirmed = false;
    private final Contact contact;
    private final boolean isEdit;

    private static final String[] CATEGORIES = {"General", "Family", "Friends", "Work", "VIP"};

    private static final String EMAIL_PLACEHOLDER = "example@mail.com";

    public ContactDialog(Frame parent, String title, Contact existing) {
        super(parent, title, true);
        this.isEdit = (existing != null);
        this.contact = isEdit ? existing : new Contact();

        buildUI();
        if (isEdit) populateFields(existing);

        pack();
        setMinimumSize(new Dimension(550, 500));
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void buildUI() {
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        UITheme.GradientPanel header = new UITheme.GradientPanel();
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(20, 25, 20, 25));
        header.setPreferredSize(new Dimension(0, 80));

        JLabel titleLbl = new JLabel(isEdit ? "✏  Edit Contact" : "＋ Add Contact");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subLbl = new JLabel(isEdit ? "Update details for ID: " + contact.getId()
                : "Enter information for the new contact");
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLbl);
        textPanel.add(subLbl);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.weightx = 1.0;

        // Row 1
        gbc.gridy = 0; gbc.gridx = 0;
        form.add(createFieldLabel("👤  Name"), gbc);
        gbc.gridx = 1;
        form.add(createFieldLabel("📞  Phone"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        nameField = UITheme.styledField("Enter full name");
        form.add(nameField, gbc);

        gbc.gridx = 1;
        phoneField = UITheme.styledField("Enter phone number");
        form.add(phoneField, gbc);

        // Row 2
        gbc.gridy = 2; gbc.gridx = 0;
        form.add(createFieldLabel("📧  Email"), gbc);

        gbc.gridx = 1;
        form.add(createFieldLabel("🏷  Category"), gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        emailField = UITheme.styledField(EMAIL_PLACEHOLDER);
        form.add(emailField, gbc);

        gbc.gridx = 1;
        categoryBox = UITheme.styledCombo(CATEGORIES);
        form.add(categoryBox, gbc);

        // Row 3
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        form.add(createFieldLabel("📝  Notes"), gbc);

        gbc.gridy = 5;
        notesArea = UITheme.styledArea(4);
        JScrollPane scroll = new JScrollPane(notesArea);
        scroll.setBorder(new UITheme.RoundedBorder(8, UITheme.BORDER));
        scroll.setBackground(UITheme.BG_INPUT);
        form.add(scroll, gbc);

        return form;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 0, 10, 20));

        UITheme.StyledButton cancelBtn =
                new UITheme.StyledButton("Cancel", UITheme.BG_CARD, UITheme.BORDER);
        cancelBtn.setPreferredSize(new Dimension(100, 38));
        cancelBtn.addActionListener(e -> dispose());

        UITheme.StyledButton saveBtn =
                new UITheme.StyledButton(isEdit ? "Update" : "Save", UITheme.ACCENT, UITheme.ACCENT_GLOW);
        saveBtn.setPreferredSize(new Dimension(120, 38));
        saveBtn.addActionListener(e -> onSave());

        footer.add(cancelBtn);
        footer.add(saveBtn);
        return footer;
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        return lbl;
    }

    private void populateFields(Contact c) {
        nameField.setText(c.getName());
        phoneField.setText(c.getPhone());

        String email = c.getEmail();
        emailField.setText(email == null ? EMAIL_PLACEHOLDER : email);

        categoryBox.setSelectedItem(c.getCategory());
    }

    // ================= VALIDATION =================

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    private boolean isValidEmail(String email) {
        return email != null &&
                email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    // ================= SAVE LOGIC =================

    private void onSave() {

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        String email = emailField.getText().trim();

        // ✅ FIX: treat placeholder / empty as NULL
        if (email.isEmpty() || email.equals(EMAIL_PLACEHOLDER)) {
            email = null;
        }

        String category = (String) categoryBox.getSelectedItem();

        // ===== VALIDATION =====

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name cannot be empty!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid phone number!\nEnter exactly 10 digits.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (email != null && !isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid email format!\nExample: user@gmail.com",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ===== SAVE DATA =====
        contact.setName(name);
        contact.setPhone(phone);
        contact.setEmail(email); // safe null
        contact.setCategory(category);

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Contact getContact() {
        return contact;
    }
}