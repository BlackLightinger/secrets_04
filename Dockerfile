# Используем образ с OpenJDK 21
FROM openjdk:21-jdk-slim

# Рабочая директория для приложения
WORKDIR /app

# Копируем проект в контейнер
COPY . /app

# Устанавливаем зависимости, если необходимо
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-8.0-bin.zip && \
    unzip gradle-8.0-bin.zip && \
    mv gradle-8.0 /opt/gradle

# Копируем gradle wrapper
COPY gradle /gradle


# Ожидаем, что jar файл будет собран на этапе локальной сборки
# Просто запускаем сервер через команду `java -jar` или с помощью gradle
CMD ["./gradlew", "run"]
