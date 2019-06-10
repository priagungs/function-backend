package com.future.function.validation.validator.core;

import com.future.function.model.entity.feature.core.FileV2;
import com.future.function.service.api.feature.core.ResourceService;
import com.future.function.validation.annotation.core.FileMustExist;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileMustExistValidatorTest {
  
  @Mock
  private FileMustExist annotation;
  
  @Mock
  private ResourceService resourceService;
  
  @InjectMocks
  private FileMustExistValidator validator;
  
  @Before
  public void setUp() {
    
    validator.initialize(annotation);
  }
  
  @After
  public void tearDown() {
    
    verifyNoMoreInteractions(annotation, resourceService);
  }
  
  @Test
  public void testGivenNullValueByValidatingFileMustExistReturnTrue() {
    
    assertThat(validator.isValid(null, null)).isTrue();
    
    verifyZeroInteractions(resourceService);
  }
  
  @Test
  public void testGivenEmptyListByValidatingFileMustExistReturnTrue() {
    
    assertThat(validator.isValid(Collections.emptyList(), null)).isTrue();
    
    verifyZeroInteractions(resourceService);
  }
  
  @Test
  public void testGivenListOfFileIdsByValidatingFileMustExistReturnTrue() {
    
    String fileId = "file-id";
    
    when(resourceService.getFile(fileId)).thenReturn(new FileV2());
    
    assertThat(
      validator.isValid(Collections.singletonList(fileId), null)).isTrue();
    
    verify(resourceService).getFile(fileId);
  }
  
  @Test
  public void testGivenListOfNonExistingFileIdsByValidatingFileMustExistReturnfLASE() {
    
    String fileId = "file-id";
    
    when(resourceService.getFile(fileId)).thenReturn(null);
    
    assertThat(
      validator.isValid(Collections.singletonList(fileId), null)).isFalse();
    
    verify(resourceService).getFile(fileId);
  }
  
}