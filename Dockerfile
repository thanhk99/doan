# Sử dụng image OpenJDK 23 làm base
FROM openjdk:23-jdk-slim

# Cài đặt Maven
RUN apt-get update && apt-get install -y maven

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép file pom.xml và src để build
COPY pom.xml .
COPY src ./src

# Build ứng dụng
RUN mvn clean package -DskipTests

# Sao chép file JAR đã build
RUN mv target/doan-0.0.1-SNAPSHOT.jar app.jar

# Mở cổng mà ứng dụng sẽ chạy
EXPOSE 8082

# Lệnh chạy ứng dụng với --enable-preview
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]