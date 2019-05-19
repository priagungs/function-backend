package com.future.function.web.mapper.response.scoring;

import com.future.function.model.entity.feature.scoring.StudentQuiz;
import com.future.function.web.mapper.helper.PageHelper;
import com.future.function.web.mapper.helper.ResponseHelper;
import com.future.function.web.model.response.base.DataResponse;
import com.future.function.web.model.response.base.PagingResponse;
import com.future.function.web.model.response.feature.scoring.StudentQuizWebResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

public class StudentQuizResponseMapper {

    public static DataResponse<StudentQuizWebResponse> toStudentQuizWebResponse(StudentQuiz studentQuiz) {
        return ResponseHelper.toDataResponse(HttpStatus.OK, buildStudentQuizWebResponse(studentQuiz));
    }

    private static StudentQuizWebResponse buildStudentQuizWebResponse(StudentQuiz studentQuiz) {
        return StudentQuizWebResponse
                .builder()
                .quiz(QuizResponseMapper.toQuizWebDataResponse(studentQuiz.getQuiz()).getData())
                .status(studentQuiz.isDone() ? "DONE" : "ONGOING")
                .build();
    }

    public static DataResponse<StudentQuizWebResponse> toStudentQuizWebResponse(HttpStatus httpStatus,
                                                                                StudentQuiz studentQuiz) {
        return ResponseHelper.toDataResponse(httpStatus, buildStudentQuizWebResponse(studentQuiz));
    }

    public static PagingResponse<StudentQuizWebResponse> toPagingStudentQuizWebResponse(Page<StudentQuiz> studentQuizPage) {
        return ResponseHelper.toPagingResponse(HttpStatus.OK,
                buildListOfStudentQuizWebResponse(studentQuizPage),
                PageHelper.toPaging(studentQuizPage));
    }

    private static List<StudentQuizWebResponse> buildListOfStudentQuizWebResponse(Page<StudentQuiz> studentQuizPage) {
        return studentQuizPage.getContent()
                .stream()
                .map(StudentQuizResponseMapper::buildStudentQuizWebResponse)
                .collect(Collectors.toList());
    }

}
