package com.future.function.web.controller.core;

import com.future.function.model.entity.feature.core.Batch;
import com.future.function.service.api.feature.core.BatchService;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(BatchController.class)
public class BatchControllerTest {

  private static final Long FIRST_BATCH_NUMBER = 1L;

  private static final Long SECOND_BATCH_NUMBER = 2L;

  private static final Batch FIRST_BATCH = Batch.builder()
          .number(FIRST_BATCH_NUMBER)
          .build();

  private static final Batch SECOND_BATCH = Batch.builder()
          .number(SECOND_BATCH_NUMBER)
          .build();

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BatchService batchService;

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {

    verifyNoMoreInteractions(batchService);
  }

  @Test
  public void testGivenCallToBatchesApiByFindingBatchesFromBatchServiceReturnListOfBatchNumbers()
          throws Exception {

    List<Batch> batches = Arrays.asList(FIRST_BATCH, SECOND_BATCH);
    given(batchService.getBatches()).willReturn(batches);

    MockHttpServletResponse response = mockMvc.perform(get("/api/core/batches"))
            .andReturn()
            .getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isNotBlank();

    verify(batchService).getBatches();
  }

  @Test
  public void testGivenCallToBatchesApiByCreatingBatchReturnNewBatchResponse()
          throws Exception {

    given(batchService.createBatch()).willReturn(FIRST_BATCH);

    MockHttpServletResponse response = mockMvc.perform(
            post("/api/core/batches"))
            .andReturn()
            .getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.getContentAsString()).isNotBlank();

    verify(batchService).createBatch();
  }

}
