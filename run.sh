git pull origin master
mvn clean package dockerfile:build
docker-compose down
docker-compose up -d