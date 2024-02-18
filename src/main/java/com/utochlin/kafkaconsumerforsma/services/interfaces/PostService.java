package com.utochlin.kafkaconsumerforsma.services.interfaces;


import com.utochlin.kafkaconsumerforsma.dto.PostDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface PostService {

    void notificationSenderPostToEmail(ConsumerRecord<Long, PostDto> record);
}
