# java-shareit

![Static Badge](https://img.shields.io/badge/Java-21-green)
![Static Badge](https://img.shields.io/badge/Spring_Boot-3.5.9-green)
![Static Badge](https://img.shields.io/badge/RestTemplate-blue)
![Static Badge](https://img.shields.io/badge/Lombok-red)
![Static Badge](https://img.shields.io/badge/QueryDSL-5.1.0-blue)
![Static Badge](https://img.shields.io/badge/PostgreSQL-16.1-blue)
![Static Badge](https://img.shields.io/badge/docker_compose-blue)
![Static Badge](https://img.shields.io/badge/H2_database-blue)
![Static Badge](https://img.shields.io/badge/JUnit-5-orange)
![Static Badge](https://img.shields.io/badge/Mockito-green)
![Static Badge](https://img.shields.io/badge/Jacoco-red)
![Static Badge](https://img.shields.io/badge/Maven-orange)
![Static Badge](https://img.shields.io/badge/Multi--module--project-8A2BE2)

## Бэкэнд для сервиса шеринга вещей
![idea](base-idea.png)

### Основные возможности
- Поиск вещей для аренды
- Публикация вещей для сдачи в аренду
- Создание запросов на вещь, если ее нет
- Отзывы о вещах взятых в аренду ранее

### Архитектура
![arch](arcitecture.png)

### Схема базы данных
![db](database-map.png)

### API
```mermaid
mindmap
  root((API))
    BookingController 🌐/bookings
      GET /bookings/:bookingId
      GET /bookings
      GET /bookings/owner
      PATCH /bookings/:bookingId
      POST /bookings
    ItemController 🌐/items
      GET /items/:itemId
      GET /items
      GET /items/search
      POST /items
      POST /items/:itemId/comment
      PATCH /items/:itemId
      DELETE /items/:itemId
    ItemRequestController 🌐/requests
      GET /requests
      GET /requests/all
      GET /requests/:requestId
      POST /requests
    UserController 🌐/users
      GET /users/:userId
      GET /users
      POST /users
      PATCH /users/:userId
      DELETE /users/:userId
```

### Детали
Мультимодульный проект.  
Состоит из сервера (server) и шлюза (gateway).  
Шлюз выполняет функцию валидации входящих данных,
а также служит для масштабирования приложения.  
Сервер покрыт тестами на 100%