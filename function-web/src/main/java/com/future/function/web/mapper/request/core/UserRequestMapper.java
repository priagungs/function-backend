package com.future.function.web.mapper.request.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.function.common.enumeration.core.Role;
import com.future.function.common.exception.BadRequestException;
import com.future.function.common.validation.ObjectValidator;
import com.future.function.model.entity.feature.core.Batch;
import com.future.function.model.entity.feature.core.File;
import com.future.function.model.entity.feature.core.User;
import com.future.function.web.model.request.core.UserWebRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class UserRequestMapper {
  
  private ObjectMapper objectMapper;
  
  private ObjectValidator validator;
  
  @Autowired
  private UserRequestMapper(
    ObjectMapper objectMapper, ObjectValidator validator
  ) {
    
    this.objectMapper = objectMapper;
    this.validator = validator;
  }
  
  public User toUser(String data) {
    
    UserWebRequest request = toUserWebRequest(data);
  
    return toValidatedUser(request.getEmail(), request);
  }
  
  private User toValidatedUser(String email, UserWebRequest request) {
    
    User user = User.builder()
      .role(Role.toRole(request.getRole()))
      .email(email)
      .name(request.getName())
      .password(getDefaultPassword(request.getName()))
      .phone(request.getPhone())
      .address(request.getAddress())
      .picture(new File())
      .batch(toBatch(request))
      .university(getUniversity(request))
      .build();
    
    return validator.validate(user);
  }
  
  private String getUniversity(UserWebRequest request) {
    
    return Optional.of(request)
      .map(UserWebRequest::getUniversity)
      .orElse(null);
  }
  
  private Batch toBatch(UserWebRequest request) {
    
    return Optional.of(request)
      .map(UserWebRequest::getBatch)
      .map(batchNumber -> Batch.builder()
        .number(batchNumber)
        .build())
      .orElse(null);
  }
  
  private String getDefaultPassword(String name) {
    
    return Optional.ofNullable(name)
      .map(String::toLowerCase)
      .map(n -> n.replace(" ", ""))
      .map(n -> n.concat("functionapp"))
      .orElse(null);
  }
  
  private UserWebRequest toUserWebRequest(String data) {
    
    UserWebRequest request;
    try {
      request = objectMapper.readValue(data, UserWebRequest.class);
    } catch (IOException e) {
      log.error("IOException occurred on parsing request, exception: '{}'", e);
      throw new BadRequestException("Bad Request");
    }
    return request;
  }
  
  public User toUser(String email, String data) {
    
    return toValidatedUser(email, toUserWebRequest(data));
  }
  
}
