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
# Dùng JRE thay vì JDK để giảm footprint của image runtime
FROM eclipse-temurin:21-jre

# Tạo thư mục làm việc
WORKDIR /app

# Copy file .jar đã được build từ Stage 1 (có tên là "build")
COPY --from=build /app/target/management-0.0.1-SNAPSHOT.jar app.jar

# Profile và giới hạn JVM mặc định cho instance nhỏ trên Render.
# Chừa headroom dưới mức 512 MB cho thread stack, code cache và native memory.
# Render vẫn có thể override các giá trị này bằng biến môi trường khi cần.
ENV SPRING_PROFILES_ACTIVE=render
ENV JAVA_TOOL_OPTIONS="-Xms64m -Xmx224m -Xss512k -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m -XX:MaxDirectMemorySize=32m -XX:ReservedCodeCacheSize=64m -XX:+ExitOnOutOfMemoryError"

# Mở cổng mặc định của Render khi biến PORT không được override
EXPOSE 10000

# Lệnh để chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
