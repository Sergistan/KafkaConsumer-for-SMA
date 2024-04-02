package com.utochlin.kafkaconsumerforsma.listener;

import com.utochlin.kafkaconsumerforsma.dto.PostDto;
import com.utochlin.kafkaconsumerforsma.models.Role;
import com.utochlin.kafkaconsumerforsma.models.User;
import com.utochlin.kafkaconsumerforsma.services.impl.PostServiceImpl;
import com.utochlin.kafkaconsumerforsma.utils.BaseSpringTestFull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
@Transactional
public class KafkaListenerServiceTest extends BaseSpringTestFull {
    @Autowired
    private PostServiceImpl postService;

    @Test
    void listen() {
        User user = User.builder()
                .id(1L)
                .name("Sergey")
                .password("111")
                .email("sergistan.utochkin@yandex.ru")
                .role(Role.ROLE_USER)
                .followers(new HashSet<>())
                .posts(new ArrayList<>())
                .chats(new HashSet<>())
                .build();

        PostDto postDto = PostDto.builder()
                .description("New description")
                .message("New message")
                .build();

        send(postDto, user.getId());

        ConsumerRecord<Long, PostDto> actual = readOutboundMessage("topic-notification-user");

        postService.notificationSenderPostToEmail(actual);

        Assertions.assertEquals(actual.key(), user.getId());
        Assertions.assertEquals(actual.value(), postDto);
    }

}
