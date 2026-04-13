# Stage 1: Giai đoạn Build (Dùng Maven để build ra file .jar)
# Sử dụng một image có sẵn Maven và JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build

# Tạo thư mục làm việc
WORKDIR /app

# Copy file pom.xml trước để tận dụng cache
COPY pom.xml .

# Copy toàn bộ mã nguồn
COPY src ./src

# Chạy lệnh build của Maven
# -DskipTests để bỏ qua việc chạy test, giúp build nhanh hơn
RUN mvn clean package -DskipTests


# Stage 2: Giai đoạn Runtime (Chạy ứng dụng)
# Sử dụng image JDK 21 gọn nhẹ hơn
FROM eclipse-temurin:21-jdk

# Tạo thư mục làm việc
WORKDIR /app

# Copy file .jar đã được build từ Stage 1 (có tên là "build")
COPY --from=build /app/target/management-0.0.1-SNAPSHOT.jar app.jar

# Mở cổng 8080 (cổng Spring Boot của bạn đang chạy)
EXPOSE 8080

# Lệnh để chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
