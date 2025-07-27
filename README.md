# ShareIt

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**ShareIt** — сервис для обмена вещами между пользователями с возможностью бронирования, отзывов, запросов на новые предметы и интеграционного тестирования.

---

## Содержание

- [Технологии](#технологии)
- [Структура проекта](#структура-проекта)
- [Возможности](#возможности)
- [Тестирование](#тестирование)
- [Запуск проекта](#запуск-проекта)

## Технологии

- **Язык и фреймворк:**
  - Java 21
  - Spring Boot 3.3.2
  - Maven 3.8

- **Базы данных:**
  - PostgreSQL (основная БД, модуль server)
  - H2 Database (тесты, модуль server)

- **Работа с данными:**
  - Spring Data JPA (модуль server)
  - Hibernate Validator (модуль gateway)
  - Apache HttpClient5 (модуль gateway)

- **Тестирование:**
  - JUnit
  - MockMVC
  - @JsonTest

- **Прочее:**
  - REST API
  - Lombok
  - Docker, Docker Compose

---

## Структура проекта

- **server/** — основной сервер, реализует бизнес-логику, работу с базой данных, REST API, интеграционные и REST-тесты (порт 9090).
- **gateway/** — шлюз, принимает запросы от клиентов, валидирует данные, перенаправляет запросы на сервер (порт 8080).

---

## Возможности

- Добавление, редактирование и просмотр вещей
- Поиск и бронирование вещей с подтверждением владельцем
- Оставление отзывов после аренды
- Запросы на добавление новых вещей и ответы на них
- Просмотр информации о вещах, бронированиях, отзывах и запросах
- Микросервисная архитектура: gateway (валидация и проксирование) + server (бизнес-логика, БД)
- Валидация данных на уровне gateway
- Разделение портов: gateway — 8080, server — 9090

---

## Тестирование

- Интеграционные тесты для сервисов (работа с БД)
- Тесты REST-эндпоинтов с использованием MockMVC
- Тесты сериализации/десериализации DTO с помощью @JsonTest
- Для ручного тестирования REST API использовался Postman (коллекция запросов находится в папке `postman/` или приложена к репозиторию)

---

## Запуск проекта

### Через Docker Compose

1. Соберите jar-файлы для gateway и server:
   ```bash
   mvn clean package
   ```
   (или используйте свой способ сборки)

2. Запустите все сервисы:
   ```bash
   docker compose up --build
   ```
   или (если файл называется `docker-compose.yml`):
   ```bash
   docker-compose up --build
   ```

3. Приложение будет доступно:
   - gateway: http://localhost:8080
   - server: http://localhost:9090
   - база данных: localhost:5432 (PostgreSQL)

4. Для остановки:
   ```bash
   docker compose down
   ```

---

### Альтернативный запуск вручную (без Docker)

1. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/your-username/shareit.git
   cd shareit
   ```

2. Соберите проект:
   ```bash
   mvn clean install
   ```

3. Запустите модули по отдельности (в двух разных терминалах):

   Для server (порт 9090):
   ```bash
   cd server
   mvn spring-boot:run
   ```

   Для gateway (порт 8080):
   ```bash
   cd gateway
   mvn spring-boot:run
   ```

   **Или из корня:**
   ```bash
   mvn spring-boot:run -f server/pom.xml
   mvn spring-boot:run -f gateway/pom.xml
   ```
