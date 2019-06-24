package com.future.function.web.mapper.response.scoring;

import com.future.function.model.entity.feature.scoring.QuestionBank;
import com.future.function.web.mapper.helper.PageHelper;
import com.future.function.web.mapper.helper.ResponseHelper;
import com.future.function.web.model.response.base.DataResponse;
import com.future.function.web.model.response.base.PagingResponse;
import com.future.function.web.model.response.feature.scoring.QuestionBankWebResponse;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QuestionBankResponseMapper {

  public static DataResponse<QuestionBankWebResponse> toQuestionBankWebResponse(QuestionBank questionBank) {
    return ResponseHelper.toDataResponse(HttpStatus.OK, buildQuestionBankWebResponse(questionBank));
  }

  public static DataResponse<QuestionBankWebResponse> toQuestionBankWebResponse(HttpStatus httpStatus, QuestionBank request) {
    return ResponseHelper.toDataResponse(httpStatus, buildQuestionBankWebResponse(request));
  }

  private static QuestionBankWebResponse buildQuestionBankWebResponse(QuestionBank questionBank) {
    QuestionBankWebResponse response = new QuestionBankWebResponse();
    BeanUtils.copyProperties(questionBank, response);
    return response;
  }

  public static PagingResponse<QuestionBankWebResponse> toPagingQuestionBankWebResponse(Page<QuestionBank> questionBankPage) {
    return ResponseHelper
        .toPagingResponse(
            HttpStatus.OK,
            questionBankPage
                .getContent()
                .stream()
                .map(QuestionBankResponseMapper::buildQuestionBankWebResponse)
                .collect(Collectors.toList()),
            PageHelper
                .toPaging(questionBankPage)
        );
  }

}
