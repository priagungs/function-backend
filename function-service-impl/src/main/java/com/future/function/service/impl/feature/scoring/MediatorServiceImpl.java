package com.future.function.service.impl.feature.scoring;

import com.future.function.common.enumeration.core.Role;
import com.future.function.model.entity.feature.core.Batch;
import com.future.function.model.entity.feature.core.User;
import com.future.function.service.api.feature.scoring.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Lazy
public class MediatorServiceImpl implements MediatorService {

    private static final Pageable MAX_PAGEABLE = new PageRequest(0, Integer.MAX_VALUE);

    @Autowired
    private QuizService quizService;

    @Autowired
    private StudentQuizService studentQuizService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private RoomService roomService;

    @Override
    public User createQuizAndAssignmentsByStudent(User user) {
        return Optional.ofNullable(user)
                .filter(student -> student.getRole().equals(Role.STUDENT))
                .map(User::getBatch)
                .map(Batch::getCode)
                .map(code -> this.findQuizAndAssignmentAndCreateForStudent(user, code, MAX_PAGEABLE))
                .orElse(user);
    }

    private User findQuizAndAssignmentAndCreateForStudent(User user, String batchCode, Pageable pageable) {
        quizService.findAllByBatchCodeAndPageable(batchCode, pageable).getContent()
                .forEach(quiz -> {
                    try {
                        studentQuizService.createStudentQuizAndSave(user, quiz);
                    } catch (Exception ignore) {
                    }
                });
        assignmentService.findAllByBatchCodeAndPageable(batchCode, pageable).getContent()
                .forEach(assignment -> {
                    try {
                        roomService.createRoomForUserAndSave(user, assignment);
                    } catch (Exception ignore) {
                    }
                });
        return user;
    }
}
