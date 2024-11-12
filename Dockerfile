# Используем базовый образ с Java 17 и Maven
FROM maven:3.8.1-openjdk-17-slim

# Копируем файлы проекта в контейнер
COPY . /app

# Устанавливаем рабочую директорию
WORKDIR /app

# Собираем проект с помощью Maven
RUN mvn clean package -DskipTests

# Устанавливаем dockerize
RUN apt-get update && apt-get install -y wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-v0.6.1.tar.gz \
    && rm dockerize-linux-amd64-v0.6.1.tar.gz

# Указываем порт, который будет использоваться приложением
EXPOSE 8080

# Запускаем dockerize перед запуском приложения
CMD ["sh", "-c", "dockerize -wait http://elasticsearch:9200 -timeout 60s java -jar target/crypto-data-collector-1.0-SNAPSHOT-jar-with-dependencies.jar"]