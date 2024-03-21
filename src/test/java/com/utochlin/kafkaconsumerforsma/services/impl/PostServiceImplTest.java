package com.utochlin.kafkaconsumerforsma.services.impl;

import com.utochlin.kafkaconsumerforsma.dto.PostDto;
import com.utochlin.kafkaconsumerforsma.models.Post;
import com.utochlin.kafkaconsumerforsma.models.Role;
import com.utochlin.kafkaconsumerforsma.models.User;
import com.utochlin.kafkaconsumerforsma.repositories.UserRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {
    @Value("${spring.mail.username}")
    private String from;
    @InjectMocks
    private PostServiceImpl postService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JavaMailSender javaMailSender;
    private User user1;
    private User user2;
    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("Sergey")
                .email("sergistan.utochkin@yandex.ru")
                .role(Role.ROLE_USER)
                .followers(new HashSet<>())
                .posts(new ArrayList<>())
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Ilya")
                .email("dzaga73i98@gmail.com")
                .role(Role.ROLE_USER)
                .followers(new HashSet<>())
                .posts(new ArrayList<>())
                .build();

        post = Post.builder()
                .id(1L)
                .createdAt(LocalDateTime.parse("2024-03-19T16:58"))
                .description("Description 1")
                .message("Message 1")
                .imageName("UUID + Date + picture1.jpg")
                .imageLink("http://localhost:9000/images/UUID_Date_picture1.jpg")
                .user(user2)
                .build();

        postDto = PostDto.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .description(post.getDescription())
                .message(post.getMessage())
                .imageName(post.getImageName())
                .imageLink(post.getImageLink())
                .authorName(post.getUser().getName())
                .build();
    }

    @Test
    void notificationSenderPostToEmail() {
        user2.setFollowers(Set.of(user1));
        user2.setPosts(List.of(post));

        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user2));

        Assertions.assertTrue(user2.getFollowers().contains(user1));

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(user1.getEmail());
        simpleMailMessage.setSubject("Заголовок: " + post.getDescription());
        simpleMailMessage.setText("Новое сообщение по подписке. " +
                System.lineSeparator() + "Сообщение: " + post.getMessage() +
                System.lineSeparator() + "Ссылка на картинку: " + post.getImageLink()
                + System.lineSeparator() + "Дата публикации: " + post.getCreatedAt().toString().replace("T", " "));

        ConsumerRecord<Long, PostDto> record = new ConsumerRecord<>("topic-notification-user", 3, 0, user1.getId(), postDto);

        postService.notificationSenderPostToEmail(record);

        verify(javaMailSender, times(1)).send(simpleMailMessage);
    }

    @Test
    void notificationSenderPostToEmailWhenNoFollowers() {
        user2.setPosts(List.of(post));

        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user2));

        Assertions.assertFalse(user2.getFollowers().contains(user1));

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(user1.getEmail());
        simpleMailMessage.setSubject("Заголовок: " + post.getDescription());
        simpleMailMessage.setText("Новое сообщение по подписке. " +
                System.lineSeparator() + "Сообщение: " + post.getMessage() +
                System.lineSeparator() + "Ссылка на картинку: " + post.getImageLink()
                + System.lineSeparator() + "Дата публикации: " + post.getCreatedAt().toString().replace("T", " "));

        ConsumerRecord<Long, PostDto> record = new ConsumerRecord<>("topic-notification-user", 3, 0, user1.getId(), postDto);

        postService.notificationSenderPostToEmail(record);

        verify(javaMailSender, never()).send(simpleMailMessage);
    }
}