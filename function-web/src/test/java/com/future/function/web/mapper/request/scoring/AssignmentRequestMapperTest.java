package com.future.function.web.mapper.request.scoring;

import com.future.function.model.entity.feature.core.FileV2;
import com.future.function.model.entity.feature.scoring.Assignment;
import com.future.function.validation.RequestValidator;
import com.future.function.web.mapper.request.WebRequestMapper;
import com.future.function.web.model.request.scoring.AssignmentWebRequest;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentRequestMapperTest {

  private static final String ASSIGNMENT_TITLE = "assignment-title";
  private static final String ASSIGNMENT_DESCRIPTION = "assignment-description";
  private static final String ASSIGNMENT_QUESTION = "assignment-question";
  private static final String ASSIGNMENT_BATCH = "[2, 3]";
  private static final String NULL_VALUE = null;
  private static final String STRING_EMPTY = "";
  private static final String BAD_REQUEST_EXCEPTION_MSG = "Bad Request";
  private static final String ASSIGNMENT_REQUEST_JSON =
      "{\n" + "\"title\": \"" + ASSIGNMENT_TITLE + "\",\n" + "    \"description\": \"" +
          ASSIGNMENT_DESCRIPTION + "\",\n" + "    \"question\": \"" + ASSIGNMENT_QUESTION + "\",\n" +
          "    \"deadline\": " + null + ",\n" + "    \"batch\": " + ASSIGNMENT_BATCH +
          "}";
  private static String ASSIGNMENT_ID;
  private Assignment assignment;
  private AssignmentWebRequest assignmentWebRequest;

  @Mock
  private WebRequestMapper requestMapper;

  @InjectMocks
  private AssignmentRequestMapper assignmentRequestMapper;

  @Mock
  private RequestValidator validator;

  @Before
  public void setUp() {
    assignment = Assignment
        .builder()
        .id(null)
        .title(ASSIGNMENT_TITLE)
        .description(ASSIGNMENT_DESCRIPTION)
        .build();

    assignmentWebRequest = AssignmentWebRequest
        .builder()
        .title(ASSIGNMENT_TITLE)
        .description(ASSIGNMENT_DESCRIPTION)
        .files(new ArrayList<>())
        .build();

    when(validator.validate(assignmentWebRequest))
        .thenReturn(assignmentWebRequest);
    when(requestMapper.toWebRequestObject(ASSIGNMENT_REQUEST_JSON, AssignmentWebRequest.class))
        .thenReturn(assignmentWebRequest);
  }

  @After
  public void tearDown() {
    verifyNoMoreInteractions(requestMapper);
    verifyNoMoreInteractions(validator);
  }

  @Test
  public void testToAssignmentFromStringDataJson() {
    Assignment actual = assignmentRequestMapper.toAssignment(assignmentWebRequest);
    assertThat(actual.getDescription()).isEqualTo(assignment.getDescription());
    assertThat(actual.getTitle()).isEqualTo(assignment.getTitle());
    verify(validator).validate(assignmentWebRequest);
  }

  @Test
  public void testToAssignmentFromStringDataJsonFileExist() {
    assignmentWebRequest.setFiles(Collections.singletonList("file-id"));
    assignment.setFile(FileV2.builder().id("file-id").build());
    Assignment actual = assignmentRequestMapper.toAssignment(assignmentWebRequest);
    assertThat(actual.getDescription()).isEqualTo(assignment.getDescription());
    assertThat(actual.getTitle()).isEqualTo(assignment.getTitle());
    verify(validator).validate(assignmentWebRequest);
  }

  @Test
  public void testToAssignmentFromStringDataJsonAndStringIdSuccess() {
    assignment.setId(ASSIGNMENT_ID);
    assignmentWebRequest.setId(ASSIGNMENT_ID);
    Assignment actual = assignmentRequestMapper.toAssignmentWithId(ASSIGNMENT_ID, assignmentWebRequest);
    assertThat(actual.getDescription()).isEqualTo(assignment.getDescription());
    assertThat(actual.getTitle()).isEqualTo(assignment.getTitle());
    verify(validator).validate(assignmentWebRequest);
  }
}