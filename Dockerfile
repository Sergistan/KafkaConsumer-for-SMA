FROM openjdk:17-jdk-slim
EXPOSE 8081
ADD build/libs/KafkaConsumer-for-SMA-0.0.1-SNAPSHOT.jar KafkaConsumer-for-SMA.jar
CMD ["java", "-jar", "KafkaConsumer-for-SMA.jar"]
