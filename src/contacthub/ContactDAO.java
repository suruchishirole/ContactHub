package contacthub;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO 
{
    // ── INSERT ────────────────────────────────────────────────────────────────
    public boolean addContact(Contact c) throws SQLException {
        String sql = "INSERT INTO contacts (name, phone, email, category, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getCategory());
            ps.setString(5, c.getNotes());
            return ps.executeUpdate() > 0;
        }
    }

    // ── SELECT ALL ────────────────────────────────────────────────────────────
    public List<Contact> getAllContacts() throws SQLException {
        String sql = "SELECT * FROM contacts ORDER BY name ASC";
        List<Contact> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── SELECT with search ────────────────────────────────────────────────────
    public List<Contact> searchContacts(String query) throws SQLException {
        String sql = """
                SELECT * FROM contacts
                WHERE  name  LIKE ?
                   OR  phone LIKE ?
                   OR  email LIKE ?
                ORDER BY name ASC
                """;
        String q = "%" + query + "%";
        List<Contact> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── SELECT by category ────────────────────────────────────────────────────
    public List<Contact> getByCategory(String category) throws SQLException {
        String sql = "SELECT * FROM contacts WHERE category = ? ORDER BY name ASC";
        List<Contact> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean updateContact(Contact c) throws SQLException {
        String sql = "UPDATE contacts SET name=?, phone=?, email=?, category=?, notes=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getCategory());
            ps.setString(5, c.getNotes());
            ps.setInt(6, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean deleteContact(int id) throws SQLException {
        String sql = "DELETE FROM contacts WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── COUNT ─────────────────────────────────────────────────────────────────
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM contacts";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── map ResultSet row → Contact ───────────────────────────────────────────
    private Contact mapRow(ResultSet rs) throws SQLException {
        Contact c = new Contact();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setCategory(rs.getString("category"));
        c.setNotes(rs.getString("notes"));
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        if (created != null) c.setCreatedAt(created.toLocalDateTime());
        if (updated != null) c.setUpdatedAt(updated.toLocalDateTime());
        return c;
    }
}
