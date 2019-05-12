package com.future.function.web.mapper.request.core;

import com.future.function.model.entity.feature.core.Course;
import com.future.function.validation.RequestValidator;
import com.future.function.web.model.request.core.CourseWebRequestV2;
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
public class CourseRequestMapperV2Test {
  
  private static final String ID = "id";
  
  private static final String TITLE = "title";
  
  private static final String DESCRIPTION = "description";
  
  private static final Course COURSE = Course.builder()
    .id(ID)
    .title(TITLE)
    .description(DESCRIPTION)
    .build();
  
  private static final CourseWebRequestV2 COURSE_WEB_REQUEST =
    CourseWebRequestV2.builder()
      .title(TITLE)
      .description(DESCRIPTION)
      .build();
  
  @Mock
  private RequestValidator validator;
  
  @InjectMocks
  private CourseRequestMapperV2 courseRequestMapperV2;
  
  @Before
  public void setUp() {
    
    when(validator.validate(COURSE_WEB_REQUEST)).thenReturn(COURSE_WEB_REQUEST);
  }
  
  @After
  public void tearDown() {
    
    verifyNoMoreInteractions(validator);
  }
  
  @Test
  public void testGivenCourseWebRequestByParsingToCourseClassReturnCourseObject() {
    
    Course parsedCourse = courseRequestMapperV2.toCourse(COURSE_WEB_REQUEST);
    
    assertThat(parsedCourse).isNotNull();
    assertThat(parsedCourse.getId()).isNotBlank();
    assertThat(parsedCourse.getTitle()).isEqualTo(TITLE);
    assertThat(parsedCourse.getDescription()).isEqualTo(DESCRIPTION);
    
    verify(validator).validate(COURSE_WEB_REQUEST);
  }
  
  @Test
  public void testGivenCourseIdAndCourseWebRequestByParsingToCourseClassReturnCourseObject() {
    
    Course parsedCourse = courseRequestMapperV2.toCourse(ID,
                                                         COURSE_WEB_REQUEST
    );
    
    assertThat(parsedCourse).isNotNull();
    assertThat(parsedCourse).isEqualTo(COURSE);
    
    verify(validator).validate(COURSE_WEB_REQUEST);
  }
  
}