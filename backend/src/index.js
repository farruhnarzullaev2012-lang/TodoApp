const express = require('express');
const db = require('./db');
const tasksRouter = require('./routes/tasks');

const app = express();
const PORT = 3000;

app.use(express.json());

// Oddiy health-check — server ishlayotganini tekshirish uchun
app.get('/', (req, res) => {
  res.json({ status: 'ok', message: 'Todo backend ishlayapti' });
});

app.use('/tasks', tasksRouter);

app.listen(PORT, () => {
  console.log(`Server http://localhost:${PORT} manzilida ishga tushdi`);
});
