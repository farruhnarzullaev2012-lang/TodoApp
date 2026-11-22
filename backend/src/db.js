const path = require('path');
const fs = require('fs');
const Database = require('better-sqlite3');

// SQLite fayli data/ papkasida saqlanadi — papka mavjud bo'lmasa, yaratiladi
const dataDir = path.join(__dirname, '..', 'data');
if (!fs.existsSync(dataDir)) {
  fs.mkdirSync(dataDir, { recursive: true });
}

const dbPath = path.join(dataDir, 'todo.db');
const db = new Database(dbPath);

// Server ishga tushganda "tasks" jadvali mavjud bo'lmasa — yaratiladi
db.exec(`
  CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    done INTEGER NOT NULL DEFAULT 0
  )
`);

module.exports = db;
