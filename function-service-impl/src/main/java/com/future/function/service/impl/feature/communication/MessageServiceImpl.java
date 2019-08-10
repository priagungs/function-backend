package com.future.function.service.impl.feature.communication;

import com.future.function.common.exception.NotFoundException;
import com.future.function.model.entity.feature.communication.chatting.Chatroom;
import com.future.function.model.entity.feature.communication.chatting.Message;
import com.future.function.repository.feature.communication.chatting.MessageRepository;
import com.future.function.service.api.feature.communication.ChatroomService;
import com.future.function.service.api.feature.communication.MessageService;
import com.future.function.service.api.feature.core.UserService;
import com.future.function.service.impl.helper.PageHelper;
import com.future.function.session.model.Session;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * Author: PriagungSatyagama
 * Created At: 17:06 04/06/2019
 */
@Service
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;

  private final ChatroomService chatroomService;

  private final UserService userService;

  @Autowired
  public MessageServiceImpl(MessageRepository messageRepository, ChatroomService chatroomService, UserService userService) {
    this.messageRepository = messageRepository;
    this.chatroomService = chatroomService;
    this.userService = userService;
  }

  @Override
  public Message getMessage(String messageId) {
    return Optional.of(messageId)
            .map(messageRepository::findOne)
            .orElseThrow(() -> new NotFoundException("Message not found"));
  }

  @Override
  public Page<Message> getMessages(String chatroomId, Pageable pageable, Session session) {
    if (chatroomId.equalsIgnoreCase("public")) {
      Chatroom publicChatroom = chatroomService.getPublicChatroom();
      chatroomId = publicChatroom.getId();
    }
    return Optional.of(chatroomId)
            .map(id -> chatroomService.getChatroom(id, session))
            .map(chatroom -> messageRepository.findAllByChatroomOrderByCreatedAtDesc(chatroom, pageable))
            .orElse(PageHelper.empty(pageable));
  }

  @Override
  public Page<Message> getMessagesAfterPivot(String chatroomId, String messageId, Pageable pageable, Session session) {
    if (chatroomId.equalsIgnoreCase("public")) {
      Chatroom publicChatroom = chatroomService.getPublicChatroom();
      chatroomId = publicChatroom.getId();
    }
    return Optional.of(chatroomId)
            .map(id -> chatroomService.getChatroom(id, session))
            .map(chatroom -> messageRepository
                    .findAllByChatroomAndIdGreaterThanOrderByCreatedAtDesc(chatroom, new ObjectId(messageId), pageable))
            .orElse(PageHelper.empty(pageable));
  }

  @Override
  public Page<Message> getMessagesBeforePivot(String chatroomId, String messageId, Pageable pageable, Session session) {
    if (chatroomId.equalsIgnoreCase("public")) {
      Chatroom publicChatroom = chatroomService.getPublicChatroom();
      chatroomId = publicChatroom.getId();
    }
    return Optional.of(chatroomId)
            .map(id -> chatroomService.getChatroom(id, session))
            .map(chatroom -> messageRepository
                    .findAllByChatroomAndIdLessThanOrderByCreatedAtDesc(chatroom, new ObjectId(messageId), pageable))
            .orElse(PageHelper.empty(pageable));
  }

  @Override
  public Message getLastMessage(String chatroomId, Session session) {
    return Optional.of(chatroomId)
            .map(id -> chatroomService.getChatroom(id, session))
            .map(messageRepository::findFirstByChatroomOrderByCreatedAtDesc)
            .orElse(null);
  }

  @Override
  public Message createMessage(Message message, Session session) {
    return Optional.of(message)
            .map(this::setSender)
            .map(msg -> this.setChatroom(msg, session))
            .map(messageRepository::save)
            .orElseThrow(UnsupportedOperationException::new);
  }

  private Message setSender(Message message) {
    message.setSender(userService.getUser(message.getSender().getId()));
    return message;
  }

  private Message setChatroom(Message message, Session session) {
    Chatroom chatroom = chatroomService.getChatroom(message.getChatroom().getId(), session);
    chatroom.setUpdatedAt(new Date().getTime());
    message.setChatroom(chatroomService.updateChatroom(chatroom, session));
    return message;
  }

}
