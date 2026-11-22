# TodoApp

Простое приложение "Список задач" — мобильный клиент на Android (Kotlin, Jetpack Compose) + backend на Node.js.

## Стек
- **Android**: Kotlin, Jetpack Compose, Retrofit
- **Backend**: Node.js, Express, SQLite (better-sqlite3)

## Функциональность
- Получение списка задач (`GET /tasks`)
- Добавление новой задачи (`POST /tasks`)
- Отметка задачи выполненной (`PATCH /tasks/:id`)
- Удаление задачи (`DELETE /tasks/:id`)

## Запуск backend
```bash
cd backend
npm install
npm start
```
Сервер поднимается на `http://localhost:3000`.

## Запуск Android-приложения
Открыть папку `app` в Android Studio и запустить на эмуляторе/устройстве.
Эмулятор обращается к бэкенду через `http://10.0.2.2:3000` (специальный адрес для доступа к хосту с эмулятора).

## Экран
- Заголовок "Vazifalar"
- Поле ввода + кнопка "Qo'shish" (добавить)
- Список задач с чекбоксом (отметить выполненной) и кнопкой удаления
- Выполненные задачи отображаются зачёркнутым текстом
