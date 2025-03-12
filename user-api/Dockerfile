FROM amazoncorretto:21

CMD ["./mvn", "clean", "package"]

VOLUME /tmp
WORKDIR /app

ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar

EXPOSE 8082

# 애플리케이션 실행 시 -javaagent 옵션 추가
ENTRYPOINT ["java", "-jar", "app.jar"]
