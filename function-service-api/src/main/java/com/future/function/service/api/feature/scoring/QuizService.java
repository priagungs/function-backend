package com.future.function.service.api.feature.scoring;

import com.future.function.model.entity.feature.scoring.Quiz;
import java.util.Observable;
import java.util.Observer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuizService {

  Quiz findById(String id);

  Page<Quiz> findAllByBatchCodeAndPageable(String batchCode, Pageable pageable);

  Quiz copyQuizWithTargetBatchCode(String targetBatchCode, Quiz quiz);

  Quiz createQuiz(Quiz request);

  Quiz updateQuiz(Quiz request);

  void deleteById(String id);

  void addObserver(Observer observer);

}
