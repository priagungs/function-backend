package com.future.function.web.controller.core;

import com.future.function.common.enumeration.core.Role;
import com.future.function.model.entity.feature.core.Batch;
import com.future.function.model.entity.feature.core.FileV2;
import com.future.function.model.entity.feature.core.User;
import com.future.function.service.api.feature.core.UserService;
import com.future.function.web.TestHelper;
import com.future.function.web.TestSecurityConfiguration;
import com.future.function.web.mapper.helper.ResponseHelper;
import com.future.function.web.mapper.request.core.UserRequestMapper;
import com.future.function.web.mapper.response.core.UserResponseMapper;
import com.future.function.web.model.request.core.UserWebRequest;
import com.future.function.web.model.response.base.BaseResponse;
import com.future.function.web.model.response.base.DataResponse;
import com.future.function.web.model.response.base.PagingResponse;
import com.future.function.web.model.response.feature.core.UserWebResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(TestSecurityConfiguration.class)
@WebMvcTest(value = UserController.class)
public class UserControllerTest extends TestHelper {
  
  private static final String ADDRESS = "address";
  
  private static final String NAME = "name";
  
  private static final String NUMBER = "1";
  
  private static final String PHONE = "081212341234";
  
  private static final String STUDENT_EMAIL = "student@test.com";
  
  private static final String UNIVERSITY = "university";
  
  private static final User STUDENT = User.builder()
    .role(Role.STUDENT)
    .email(STUDENT_EMAIL)
    .name(NAME)
    .phone(PHONE)
    .address(ADDRESS)
    .pictureV2(new FileV2())
    .batch(Batch.builder()
             .code(NUMBER)
             .build())
    .university(UNIVERSITY)
    .build();
  
  private static final UserWebRequest STUDENT_WEB_REQUEST =
    UserWebRequest.builder()
      .role("STUDENT")
      .email(STUDENT_EMAIL)
      .name(NAME)
      .phone(PHONE)
      .address(ADDRESS)
      .batch(NUMBER)
      .university(UNIVERSITY)
      .build();
  
  private static final Pageable PAGEABLE = new PageRequest(0, 10);
  
  private static final DataResponse<UserWebResponse> RETRIEVED_DATA_RESPONSE =
    UserResponseMapper.toUserDataResponse(STUDENT);
  
  private static final DataResponse<UserWebResponse> CREATED_DATA_RESPONSE =
    UserResponseMapper.toUserDataResponse(HttpStatus.CREATED, STUDENT);
  
  private static final List<User> STUDENTS_LIST = Arrays.asList(
    STUDENT, STUDENT, STUDENT);
  
  private static final PagingResponse<UserWebResponse> PAGING_RESPONSE =
    UserResponseMapper.toUsersPagingResponse(
      new PageImpl<>(STUDENTS_LIST, PAGEABLE, STUDENTS_LIST.size()));
  
  private static final BaseResponse BASE_RESPONSE =
    ResponseHelper.toBaseResponse(HttpStatus.OK);
  
  private JacksonTester<UserWebRequest> userWebRequestJacksonTester;
  
  @MockBean
  private UserService userService;
  
  @MockBean
  private UserRequestMapper userRequestMapper;
  
  @Override
  @Before
  public void setUp() {
    
    super.setUp();
  }
  
  @After
  public void tearDown() {
    
    verifyNoMoreInteractions(userService, userRequestMapper);
  }
  
  @Test
  public void testGivenCallToUsersApiByGettingUsersFromUserServiceReturnPagingResponseOfUsers()
    throws Exception {
    
    super.setCookie(Role.ADMIN);
    
    given(userService.getUsers(Role.STUDENT, PAGEABLE)).willReturn(
      new PageImpl<>(STUDENTS_LIST, PAGEABLE, STUDENTS_LIST.size()));
    
    mockMvc.perform(get("/api/core/users").cookie(cookies)
                      .param("role", Role.STUDENT.name()))
      .andExpect(status().isOk())
      .andExpect(content().json(
        pagingResponseJacksonTester.write(PAGING_RESPONSE)
          .getJson()));
    
    verify(userService).getUsers(Role.STUDENT, PAGEABLE);
    verifyZeroInteractions(userRequestMapper);
  }
  
  @Test
  public void testGivenEmailFromPathVariableByDeletingUserByEmailReturnBaseResponseOK()
    throws Exception {
    
    super.setCookie(Role.ADMIN);
    
    mockMvc.perform(delete("/api/core/users/" + STUDENT_EMAIL).cookie(cookies))
      .andExpect(status().isOk())
      .andExpect(content().json(baseResponseJacksonTester.write(BASE_RESPONSE)
                                  .getJson()))
      .andReturn()
      .getResponse();
    
    verify(userService).deleteUser(STUDENT_EMAIL);
    verifyZeroInteractions(userRequestMapper);
  }
  
  @Test
  public void testGivenEmailFromPathVariableByGettingUserByEmailReturnDataResponseUser()
    throws Exception {
    
    super.setCookie(Role.ADMIN);
    
    given(userService.getUser(STUDENT_EMAIL)).willReturn(STUDENT);
    
    mockMvc.perform(get("/api/core/users/" + STUDENT_EMAIL).cookie(cookies))
      .andExpect(status().isOk())
      .andExpect(content().json(
        dataResponseJacksonTester.write(RETRIEVED_DATA_RESPONSE)
          .getJson()));
    
    verify(userService).getUser(STUDENT_EMAIL);
    verifyZeroInteractions(userRequestMapper);
  }
  
  @Test
  public void testGivenUserDataAsStringAndImageByCreatingUserReturnDataResponseUser()
    throws Exception {
    
    super.setCookie(Role.ADMIN);
    
    User student = User.builder()
      .role(Role.STUDENT)
      .email(STUDENT_EMAIL)
      .name(NAME)
      .phone(PHONE)
      .address(ADDRESS)
      .batch(Batch.builder()
               .code(NUMBER)
               .build())
      .university(UNIVERSITY)
      .build();
    
    given(userRequestMapper.toUser(STUDENT_WEB_REQUEST)).willReturn(student);
    given(userService.createUser(student)).willReturn(STUDENT);
    
    mockMvc.perform(post("/api/core/users").cookie(cookies)
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(
                        userWebRequestJacksonTester.write(STUDENT_WEB_REQUEST)
                          .getJson()))
      .andExpect(status().isCreated())
      .andExpect(content().json(
        dataResponseJacksonTester.write(CREATED_DATA_RESPONSE)
          .getJson()));
    
    verify(userService).createUser(student);
    verify(userRequestMapper).toUser(STUDENT_WEB_REQUEST);
  }
  
  @Test
  public void testGivenEmailFromPathVariableAndUserDataAsStringaAndImageByUpdatingUserReturnDataResponseUser()
    throws Exception {
    
    super.setCookie(Role.ADMIN);
    
    User student = User.builder()
      .role(Role.STUDENT)
      .email(STUDENT_EMAIL)
      .name(NAME)
      .phone(PHONE)
      .address(ADDRESS)
      .batch(Batch.builder()
               .code(NUMBER)
               .build())
      .university(UNIVERSITY)
      .build();
    
    given(
      userRequestMapper.toUser(STUDENT_EMAIL, STUDENT_WEB_REQUEST)).willReturn(
      student);
    given(userService.updateUser(student)).willReturn(STUDENT);
    
    mockMvc.perform(put("/api/core/users/" + STUDENT_EMAIL).cookie(cookies)
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(
                        userWebRequestJacksonTester.write(STUDENT_WEB_REQUEST)
                          .getJson()))
      .andExpect(status().isOk())
      .andExpect(content().json(
        dataResponseJacksonTester.write(RETRIEVED_DATA_RESPONSE)
          .getJson()));
    
    verify(userService).updateUser(student);
    verify(userRequestMapper).toUser(STUDENT_EMAIL, STUDENT_WEB_REQUEST);
  }
  
}
