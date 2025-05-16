# Amazon Corretto 17
FROM amazoncorretto:17

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем собранный jar-файл
COPY target/library-management-0.0.1-SNAPSHOT.jar app.jar

# Указываем команду запуска
ENTRYPOINT ["java", "-jar", "app.jar"]
