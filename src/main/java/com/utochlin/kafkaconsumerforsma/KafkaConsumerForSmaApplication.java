package com.utochlin.kafkaconsumerforsma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class KafkaConsumerForSmaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerForSmaApplication.class, args);
    }

}
