FROM amazoncorretto:21

CMD ["./mvn", "clean", "package"]

VOLUME /tmp
WORKDIR /app

ARG JAR_FILE_PATH=city-batch/target/*.jar
COPY ${JAR_FILE_PATH} app.jar

EXPOSE 8080

# 애플리케이션 실행 시 -javaagent 옵션 추가
ENTRYPOINT ["java", "-jar", "app.jar"]
