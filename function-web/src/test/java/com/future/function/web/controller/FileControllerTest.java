package com.future.function.web.controller;

import com.future.function.common.enumeration.FileOrigin;
import com.future.function.service.api.feature.file.FileService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(FileController.class)
public class FileControllerTest {
  
  @Autowired
  private MockMvc mockMvc;
  
  @MockBean
  private FileService fileService;
  
  @Before
  public void setUp() {}
  
  @After
  public void tearDown() {}
  
  @Test
  public void testGivenCallToGetFileApiByGettingFileByOriginAndFilenameReturnByteArray()
    throws Exception {
    
    String fileName = "name.png";
    String origin = "user";
    byte[] bytes = new byte[100];
    
    given(fileService.getFileAsByteArray(fileName, FileOrigin.USER)).willReturn(bytes);
  
    MockHttpServletResponse response = mockMvc.perform(
      get("/files/resource/" + origin + "/" + fileName))
      .andReturn()
      .getResponse();
    
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsByteArray()).isEqualTo(bytes);
    
    verify(fileService).getFileAsByteArray(fileName, FileOrigin.USER);
  }
  
}