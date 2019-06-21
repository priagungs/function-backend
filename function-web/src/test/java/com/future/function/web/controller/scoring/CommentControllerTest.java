package com.future.function.web.controller.scoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.function.common.enumeration.core.Role;
import com.future.function.model.entity.feature.core.Batch;
import com.future.function.model.entity.feature.core.FileV2;
import com.future.function.model.entity.feature.core.User;
import com.future.function.model.entity.feature.scoring.Assignment;
import com.future.function.model.entity.feature.scoring.Comment;
import com.future.function.model.entity.feature.scoring.Room;
import com.future.function.service.api.feature.scoring.AssignmentService;
import com.future.function.service.api.feature.scoring.RoomService;
import com.future.function.web.TestSecurityConfiguration;
import com.future.function.web.mapper.helper.ResponseHelper;
import com.future.function.web.mapper.request.scoring.CommentRequestMapper;
import com.future.function.web.mapper.response.scoring.CommentResponseMapper;
import com.future.function.web.mapper.response.scoring.RoomResponseMapper;
import com.future.function.web.model.request.scoring.CommentWebRequest;
import com.future.function.web.model.request.scoring.RoomPointWebRequest;
import com.future.function.web.model.response.base.BaseResponse;
import com.future.function.web.model.response.base.DataResponse;
import com.future.function.web.model.response.base.PagingResponse;
import com.future.function.web.model.response.feature.scoring.CommentWebResponse;
import com.future.function.web.model.response.feature.scoring.RoomWebResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(TestSecurityConfiguration.class)
@WebMvcTest(CommentController.class)
public class CommentControllerTest {

  private static final String BATCH_CODE = "3";
  private static final String ROOM_ID = "room-id";
  private static final String USER_ID = "user-id";
  private static final String USER_NAME = "user-name";
  private static final String COMMENT_ID = "comment-id";
  private static final String COMMENT = "comment";
  private static final String ASSIGNMENT_ID = "assignment-id";


  private Comment comment;
  private Room room;
  private User user;
  private List<Comment> commentList;
  private CommentWebRequest commentWebRequest;

  private DataResponse<CommentWebResponse> DATA_RESPONSE;


  private DataResponse<CommentWebResponse> CREATED_DATA_RESPONSE;

  private DataResponse<List<CommentWebResponse>> LIST_DATA_RESPONSE;

  private BaseResponse BASE_RESPONSE;

  private JacksonTester<DataResponse<CommentWebResponse>> dataResponseJacksonTester;

  private JacksonTester<DataResponse<List<CommentWebResponse>>> pagingResponseJacksonTester;

  private JacksonTester<BaseResponse> baseResponseJacksonTester;

  private JacksonTester<CommentWebRequest> roomPointWebRequestJacksonTester;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RoomService roomService;

  @MockBean
  private CommentRequestMapper commentRequestMapper;

  @Before
  public void setUp() throws Exception {

    JacksonTester.initFields(this, new ObjectMapper());

    user = User.builder().id(USER_ID)
        .name(USER_NAME)
        .address("address")
        .phone("phone")
        .email("email")
        .batch(Batch.builder().code(BATCH_CODE).build())
        .role(Role.STUDENT)
        .pictureV2(null)
        .build();

    room = Room.builder()
        .student(user)
        .point(0)
        .build();

    comment = Comment.builder().author(user).comment(COMMENT).id(COMMENT_ID).room(room).build();

    commentWebRequest = CommentWebRequest.builder().userId(USER_ID).comment(COMMENT).build();

    DATA_RESPONSE = CommentResponseMapper
        .toDataCommentWebResponse(HttpStatus.OK, this.comment);

    CREATED_DATA_RESPONSE = CommentResponseMapper
        .toDataCommentWebResponse(HttpStatus.CREATED, this.comment);

    LIST_DATA_RESPONSE = CommentResponseMapper
        .toDataListCommentWebResponse(Collections.singletonList(this.comment));

    BASE_RESPONSE = ResponseHelper.toBaseResponse(HttpStatus.OK);

    when(roomService.findAllCommentsByRoomId(ROOM_ID))
        .thenReturn(Collections.singletonList(comment));
    when(roomService.createComment(comment))
        .thenReturn(comment);
    when(commentRequestMapper.toCommentFromRequestWithRoomId(commentWebRequest, ROOM_ID))
        .thenReturn(comment);
  }

  @After
  public void tearDown() throws Exception {
    verifyNoMoreInteractions(roomService, commentRequestMapper);
  }

  @Test
  public void findAllCommentsByRoomId() throws Exception {
    mockMvc.perform(
        get("/api/scoring/batches/" + BATCH_CODE + "/assignments/" + ASSIGNMENT_ID + "/rooms/" + ROOM_ID +
            "/comments"))
        .andExpect(status().isOk())
        .andExpect(content().json(
            pagingResponseJacksonTester.write(LIST_DATA_RESPONSE).getJson()));
    verify(roomService).findAllCommentsByRoomId(ROOM_ID);
  }

  @Test
  public void createComment() throws Exception {
    mockMvc.perform(
        post("/api/scoring/batches/" + BATCH_CODE + "/assignments/" + ASSIGNMENT_ID + "/rooms/" + ROOM_ID +
            "/comments")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(roomPointWebRequestJacksonTester.write(commentWebRequest).getJson()))
        .andExpect(status().isCreated())
        .andExpect(content().json(
            dataResponseJacksonTester.write(CREATED_DATA_RESPONSE).getJson()));
    verify(roomService).createComment(comment);
    verify(commentRequestMapper).toCommentFromRequestWithRoomId(commentWebRequest, ROOM_ID);
  }
}