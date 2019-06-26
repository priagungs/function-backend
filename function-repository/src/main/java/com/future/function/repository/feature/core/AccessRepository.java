package com.future.function.repository.feature.core;

import com.future.function.model.entity.feature.core.Access;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessRepository
  extends MongoRepository<Access, String>, AccessRepositoryCustom {}
