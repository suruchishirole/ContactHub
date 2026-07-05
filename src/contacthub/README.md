# Contact Manager Pro 📇
### A Java + MySQL Desktop Application

A polished, dark-themed desktop contact management system built with Java Swing and MySQL.

---

## ✨ Features

| Feature | Description |
|---|---|
| ➕ **Add Contact** | Name, phone, email, category, notes with validation |
| 🔍 **Live Search** | Real-time search by name, phone, or email (debounced) |
| ✏️ **Edit Contact** | Double-click any row or use sidebar button |
| ❌ **Delete Contact** | With confirmation dialog (also via Delete key) |
| 🏷️ **Categories** | General, Family, Friends, Work, VIP with color badges |
| 📊 **Stats Sidebar** | Live counts by category |
| 🎨 **Dark UI** | Professional dark theme with accent colors |

---

## 🛠️ Prerequisites

| Tool | Version | Download |
|---|---|---|
| JDK | 17 or later | https://adoptium.net |
| MySQL Server | 8.0+ | https://dev.mysql.com/downloads/mysql/ |
| MySQL Connector/J | 8.x | https://dev.mysql.com/downloads/connector/j/ |

---

## 🚀 Setup & Run

### Step 1 – Project Structure
```
ContactManager/
├── src/
│   └── contactmanager/
│       ├── App.java              ← Entry point
│       ├── MainWindow.java       ← Main GUI
│       ├── ContactDialog.java    ← Add/Edit dialog
│       ├── UITheme.java          ← Design system
│       ├── Contact.java          ← Model
│       ├── ContactDAO.java       ← DB operations
│       └── DatabaseConfig.java   ← Connection config
├── lib/
│   └── mysql-connector-j-8.3.0.jar   ← ← PLACE HERE
├── setup.sql                     ← Optional: manual DB init
├── run.bat                       ← Windows launcher
└── run.sh                        ← Linux/macOS launcher
```

### Step 2 – Add JDBC Driver
1. Download **MySQL Connector/J** from https://dev.mysql.com/downloads/connector/j/
2. Extract and place `mysql-connector-j-*.jar` inside the `lib/` folder
3. Rename it to `mysql-connector-j-8.3.0.jar` (or update the name in run scripts)

### Step 3 – Configure Database Credentials
Open `src/contactmanager/DatabaseConfig.java` and update:
```java
private static final String DB_USER     = "root";       // your MySQL username
private static final String DB_PASSWORD = "";            // your MySQL password
private static final String DB_HOST     = "localhost";   // your MySQL host
private static final String DB_PORT     = "3306";        // your MySQL port
```

### Step 4 – Build & Run

**Windows:**
```bat
run.bat
```

**Linux / macOS:**
```bash
chmod +x run.sh
./run.sh
```

**Manual (any OS):**
```bash
mkdir out
javac -cp lib/mysql-connector-j-8.3.0.jar -d out src/contactmanager/*.java
java  -cp "out:lib/mysql-connector-j-8.3.0.jar" contactmanager.App
# Windows: use semicolon → out;lib/...
```

### Step 5 – Optional: Load Sample Data
```bash
mysql -u root -p < setup.sql
```
This creates 8 sample contacts across all categories.

---

## 🗄️ Database Schema

```sql
CREATE TABLE contacts (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(120)  NOT NULL,
    phone       VARCHAR(20)   NOT NULL UNIQUE,
    email       VARCHAR(120),
    category    VARCHAR(50)   DEFAULT 'General',
    notes       TEXT,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## 🎮 Keyboard Shortcuts

| Key | Action |
|---|---|
| `Enter` in search | Instant search |
| `Delete` key | Delete selected row |
| `Double-click` row | Edit contact |
| `Esc` in dialog | Cancel |
| `Enter` in dialog | Save |

---

## 🏗️ Architecture

```
App.java (Entry Point)
    └── MainWindow.java (JFrame)
            ├── Header (search + add button)
            ├── Sidebar (stats + category filter + action buttons)
            ├── Content (JTable with custom renderers)
            └── StatusBar (feedback + DB info)
                    ↕
            ContactDialog.java (Add/Edit modal)
                    ↕
            ContactDAO.java (JDBC SQL layer)
                    ↕
            DatabaseConfig.java (Connection pool)
                    ↕
            MySQL Database
```

---

## 🐛 Troubleshooting

**"MySQL JDBC Driver not found"**
→ Make sure the JAR is in `lib/` and the filename matches in run scripts.

**"Cannot Connect to MySQL"**
→ Verify MySQL is running (`mysql -u root -p`), and credentials in `DatabaseConfig.java` are correct.

**"Communications link failure"**
→ MySQL may not be started. Run `sudo service mysql start` (Linux) or start from System Preferences (macOS).

**Compilation errors on Java < 17**
→ The code uses text blocks (`"""`) which require Java 15+ (preview) or Java 17+ (stable). Update your JDK.
