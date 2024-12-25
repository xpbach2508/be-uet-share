# Thesis [2024.12.15]

Thesis: Nghiên cứu tối ưu lịch trình cho bài toán chia sẻ chuyến đi

## Description

Server `/be` provides APIs for web and app.

Database and dataset in `/dataset` provides database for web

## Author

Phạm Xuân Bách - UET CN1 '25

## Link FE

https://github.com/xpbach2508/uet-share

## Local deployment

Import database from `/dataset` to MySQL server.

Modify java spring config at `/be/src/main/resources/application.properties` with JWT secret and mysql credentials:

```properties
# JWT config
app.jwt.secret=your_jwt_secret_key

# MySQL config
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password

# WebSocket config
socket.port=8082
socket.host=localhost

# App host
server.port=8081
```
