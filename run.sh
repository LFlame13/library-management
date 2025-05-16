
echo "Собираем проект..."
mvn clean package -DskipTests

echo "Собираем Docker-образ..."
docker build -t library-management-app .

echo "Запускаем docker-compose..."
docker-compose up -d