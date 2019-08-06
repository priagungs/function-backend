package com.future.function.repository.feature.communication.questionnaire;

import com.future.function.model.entity.feature.communication.questionnaire.Questionnaire;
import com.future.function.model.entity.feature.communication.questionnaire.QuestionnaireResponse;
import com.future.function.model.entity.feature.core.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionnaireResponseRepository extends MongoRepository<QuestionnaireResponse,String> {

  /**
   * Find all questionnaire response by questionnaire
   *
   * @param questionnaire
   *
   * @return {@code List<QuestionnaireResponse>} - questionnaire response from database
   */
  List<QuestionnaireResponse> findAllByQuestionnaireAndAppraiseeAndDeletedFalse(Questionnaire questionnaire, User appraisee);

  Optional<QuestionnaireResponse> findByQuestionnaireAndAppraiseeAndAppraiserAndDeletedFalse(Questionnaire questionnaire, User appraisee, User appraiser);
}
