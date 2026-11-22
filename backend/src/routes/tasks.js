const express = require('express');
const db = require('../db');

const router = express.Router();

// GET /tasks — barcha vazifalar ro'yxatini qaytaradi
router.get('/', (req, res) => {
  const tasks = db.prepare('SELECT * FROM tasks ORDER BY id').all();
  res.json(tasks);
});

// POST /tasks — body'dan {title} olib yangi vazifa qo'shadi
router.post('/', (req, res) => {
  const { title } = req.body;

  if (!title || typeof title !== 'string' || !title.trim()) {
    return res.status(400).json({ error: 'title majburiy va bo\'sh bo\'lmasligi kerak' });
  }

  const result = db.prepare('INSERT INTO tasks (title, done) VALUES (?, 0)').run(title.trim());
  const newTask = db.prepare('SELECT * FROM tasks WHERE id = ?').get(result.lastInsertRowid);

  res.status(201).json(newTask);
});

// PATCH /tasks/:id — body'dan {done} olib holatini yangilaydi
router.patch('/:id', (req, res) => {
  const { id } = req.params;
  const { done } = req.body;

  if (done !== 0 && done !== 1) {
    return res.status(400).json({ error: 'done 0 yoki 1 bo\'lishi kerak' });
  }

  const task = db.prepare('SELECT * FROM tasks WHERE id = ?').get(id);
  if (!task) {
    return res.status(404).json({ error: 'Vazifa topilmadi' });
  }

  db.prepare('UPDATE tasks SET done = ? WHERE id = ?').run(done, id);
  const updatedTask = db.prepare('SELECT * FROM tasks WHERE id = ?').get(id);

  res.json(updatedTask);
});

// DELETE /tasks/:id — vazifani o'chiradi
router.delete('/:id', (req, res) => {
  const { id } = req.params;

  const task = db.prepare('SELECT * FROM tasks WHERE id = ?').get(id);
  if (!task) {
    return res.status(404).json({ error: 'Vazifa topilmadi' });
  }

  db.prepare('DELETE FROM tasks WHERE id = ?').run(id);
  res.status(204).send();
});

module.exports = router;
