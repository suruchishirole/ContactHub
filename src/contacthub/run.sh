#!/bin/bash
# ============================================================
#  Contact Manager Pro – Build & Run Script (Linux / macOS)
#  Requirements: JDK 17+, mysql-connector-j-8.x.x.jar
# ============================================================

DRIVER_JAR="lib/mysql-connector-j-8.3.0.jar"
SRC_DIR="src"
OUT_DIR="out"
MAIN_CLASS="contactmanager.App"

if [ ! -f "$DRIVER_JAR" ]; then
    echo "[ERROR] MySQL JDBC driver not found at $DRIVER_JAR"
    echo "  Download from: https://dev.mysql.com/downloads/connector/j/"
    echo "  Place the JAR in the lib/ folder."
    exit 1
fi

mkdir -p "$OUT_DIR"

echo "[1/3] Compiling sources..."
javac -cp "$DRIVER_JAR" -d "$OUT_DIR" "$SRC_DIR"/contactmanager/*.java
if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed."
    exit 1
fi

echo "[2/3] Compilation successful."
echo "[3/3] Launching Contact Manager Pro..."
java -cp "$OUT_DIR:$DRIVER_JAR" $MAIN_CLASS
