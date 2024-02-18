package com.utochlin.kafkaconsumerforsma.services.impl;


import com.utochlin.kafkaconsumerforsma.dto.PostDto;
import com.utochlin.kafkaconsumerforsma.exceptions.UserNotFoundException;
import com.utochlin.kafkaconsumerforsma.models.User;
import com.utochlin.kafkaconsumerforsma.repositories.UserRepository;
import com.utochlin.kafkaconsumerforsma.services.interfaces.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    @Value("${spring.mail.username}")
    private String from;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "topic-notification-user", groupId = "1")
    @Override
    public void notificationSenderPostToEmail(ConsumerRecord<Long, PostDto> record) {
        log.info("User id: {}", record.key());
        log.info("PostDto: {}", record.value());

        Set<User> followersOfUser = listFollowersOfUser(record.key());
        if (!followersOfUser.isEmpty()){
            List<String> emailsFollowersOfUser = followersOfUser.stream().map(User::getEmail).toList();

            postEmailFollowersOfUser(emailsFollowersOfUser, record.value());
        }
    }

    public Set<User> listFollowersOfUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return user.getFollowers();
    }

    public void postEmailFollowersOfUser(List<String> emailsFollowersOfUser, PostDto postDto) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        for (String email : emailsFollowersOfUser) {
            simpleMailMessage.setFrom(from);
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("Заголовок: " + postDto.getDescription());
            simpleMailMessage.setText("Новое сообщение по подписке. " +
                    System.lineSeparator() + "Сообщение: " + postDto.getMessage() +
                    System.lineSeparator() + "Ссылка на картинку: " + postDto.getImageLink()
                    + System.lineSeparator() + "Дата публикации: " + postDto.getCreatedAt().toString().replace("T", " "));
            javaMailSender.send(simpleMailMessage);
        }
    }

}
