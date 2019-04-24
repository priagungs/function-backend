package com.future.function.model.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Abstract class containing name of documents in database.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class DocumentName {
  
  public static final String SEQUENCE = "sequences";
  
  public static final String BATCH = "batchNumbers";
  
  public static final String FILE = "files";
  
  public static final String USER = "users";
  
  public static final String COURSE = "courses";
  
  public static final String SHARED_COURSE = "shared-courses";
  
}
