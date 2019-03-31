package com.future.function.common.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Component
public class ObjectValidator {
  
  private final Validator validator;
  
  @Autowired
  public ObjectValidator(Validator validator) {
    
    this.validator = validator;
  }
  
  public <T> T validate(T data) {
    
    Set<ConstraintViolation<T>> violations = validator.validate(data);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations.toString(), violations);
    } else {
      return data;
    }
  }
  
}
