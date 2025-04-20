# Sử dụng image OpenJDK 23 làm base
FROM openjdk:23-jdk-slim

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép file pom.xml và tải các dependencies
COPY pom.xml .
COPY src ./src

# Chạy lệnh build Maven
RUN apt-get update && apt-get install -y maven && mvn clean package -DskipTests

# Sao chép file JAR đã build
COPY target/doan-0.0.1-SNAPSHOT.jar app.jar

# Mở cổng mà ứng dụng sẽ chạy
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]