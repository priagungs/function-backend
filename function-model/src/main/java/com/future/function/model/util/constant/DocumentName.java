package com.future.function.model.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Abstract class containing name of documents in database.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class DocumentName {
  
  public static final String SEQUENCE = "sequences";
  
  public static final String BATCH = "batches";
  
  public static final String FILE = "files";
  
  public static final String USER = "users";

  public static final String ANNOUNCEMENT = "announcements";

  public static final String STICKY_NOTE = "sticky-notes";

  public static final String ASSIGNMENT = "assignments";

  public static final String QUESTION_BANK = "question-banks";

  public static final String QUIZ = "quizzes";

    public static final String STUDENT_QUIZ = "student-quizzes";

    public static final String STUDENT_QUIZ_DETAIL = "student-quizzes-detail";

  public static final String QUESTION = "questions";

  public static final String OPTION = "options";

}
