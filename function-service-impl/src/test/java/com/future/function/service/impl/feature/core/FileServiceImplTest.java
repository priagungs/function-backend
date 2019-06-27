package com.future.function.service.impl.feature.core;

import com.future.function.common.enumeration.core.FileOrigin;
import com.future.function.common.enumeration.core.Role;
import com.future.function.common.exception.NotFoundException;
import com.future.function.common.properties.core.FileProperties;
import com.future.function.model.entity.feature.core.FileV2;
import com.future.function.repository.feature.core.FileRepositoryV2;
import com.future.function.service.api.feature.core.ResourceService;
import com.future.function.session.model.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceImplTest {
  
  private static final String ID = "id";
  
  private static final String PARENT_ID = "parent-id";
  
  private static final String EMAIL = "email";
  
  private static final Pageable PAGEABLE = new PageRequest(0, 5);
  
  private static final String ROOT = "root";
  
  private static final String NAME = "name";
  
  private static final Session SESSION = new Session( "session-id","user-id",
                                                     EMAIL, Role.ADMIN
  );
  
  private FileV2 file = FileV2.builder()
    .id(ID)
    .parentId(PARENT_ID)
    .markFolder(false)
    .build();
  
  private Page<FileV2> page;
  
  @Mock
  private FileRepositoryV2 fileRepository;
  
  @Mock
  private ResourceService resourceService;
  
  @Mock
  private FileProperties fileProperties;
  
  @InjectMocks
  private FileServiceImpl fileService;
  
  @Before
  public void setUp() {
    
    file.setCreatedBy(EMAIL);
  }
  
  @After
  public void tearDown() {
    
    verifyNoMoreInteractions(fileRepository, resourceService, fileProperties);
  }
  
  @Test
  public void testGivenFileOrFolderIdAndParentIdByGettingFileOrFolderReturnFileOrFolder() {
    
    when(fileRepository.findByIdAndParentIdAndDeletedFalse(ID, PARENT_ID)).thenReturn(
      Optional.of(file));
    
    FileV2 file = fileService.getFileOrFolder(ID, PARENT_ID);
    
    assertThat(file).isNotNull();
    assertThat(file).isEqualTo(this.file);
    
    verify(fileRepository).findByIdAndParentIdAndDeletedFalse(ID, PARENT_ID);
    verifyZeroInteractions(resourceService, fileProperties);
  }
  
  @Test
  public void testGivenInvalidFileOrFolderIdAndParentIdByGettingFileOrFolderReturnNotFoundException() {
    
    when(fileRepository.findByIdAndParentIdAndDeletedFalse(ID, PARENT_ID)).thenReturn(
      Optional.empty());
    
    catchException(() -> fileService.getFileOrFolder(ID, PARENT_ID));
    
    assertThat(caughtException().getClass()).isEqualTo(NotFoundException.class);
    assertThat(caughtException().getMessage()).isEqualTo(
      "Get File/Folder Not Found");
    
    verify(fileRepository).findByIdAndParentIdAndDeletedFalse(ID, PARENT_ID);
    verifyZeroInteractions(resourceService, fileProperties);
  }
  
  @Test
  public void testGivenParentIdAndPageableByGettingFilesOrFoldersReturnPageOfFile() {
    
    page = new PageImpl<>(Collections.singletonList(file), PAGEABLE, 1);
    
    when(
      fileRepository.findAllByParentIdAndAsResourceFalseAndDeletedFalseOrderByMarkFolderDesc(
        PARENT_ID, PAGEABLE)).thenReturn(page);
    
    Page<FileV2> page = fileService.getFilesAndFolders(PARENT_ID, PAGEABLE);
    
    assertThat(page).isNotNull();
    assertThat(page).isEqualTo(this.page);
    
    verify(
      fileRepository).findAllByParentIdAndAsResourceFalseAndDeletedFalseOrderByMarkFolderDesc(
      PARENT_ID, PAGEABLE);
  }
  
  @Test
  public void testGivenEmailAndParentIdAndFileOrFolderIdByDeletingFileOrFolderReturnSuccessfulDeletion() {
    
    when(fileProperties.getRootId()).thenReturn(ROOT);
    when(fileRepository.findByIdAndParentIdAndDeletedFalse(ID, PARENT_ID)).thenReturn(
      Optional.of(file));
    when(fileRepository.findAllByParentIdAndDeletedFalse(ID)).thenReturn(
      Collections.emptyList());
    when(resourceService.markFilesUsed(Collections.singletonList(ID),
                                       false
    )).thenReturn(true);
    when(fileRepository.findAll(Collections.singletonList(ID))).thenReturn(
      Collections.singletonList(file));
    
    FileV2 markedDeletedFile = new FileV2();
    BeanUtils.copyProperties(file, markedDeletedFile);
    markedDeletedFile.setDeleted(true);
    when(fileRepository.save(
      Collections.singletonList(markedDeletedFile))).thenReturn(
      Collections.singletonList(markedDeletedFile));
    
    fileService.deleteFileOrFolder(SESSION, PARENT_ID, ID);
    
    verify(fileProperties).getRootId();
    verify(fileRepository).findByIdAndParentIdAndDeletedFalse(ID, PARENT_ID);
    verify(fileRepository).findAllByParentIdAndDeletedFalse(ID);
    verify(resourceService).markFilesUsed(Collections.singletonList(ID), false);
    verify(fileRepository).findAll(Collections.singletonList(ID));
    verify(fileRepository).save(Collections.singletonList(markedDeletedFile));
  }
  
  @Test
  public void testGivenEmailAndParentIdAndFileOrFolderIdAndNestedFileOrFolderByDeletingFileOrFolderReturnSuccessfulDeletion() {
    
    FileV2 folder1 = FileV2.builder()
      .id("folder-1")
      .parentId(ROOT)
      .markFolder(true)
      .build();
    FileV2 file1 = FileV2.builder()
      .id("file-1")
      .parentId(folder1.getId())
      .build();
    FileV2 folder2 = FileV2.builder()
      .id("folder-2")
      .parentId(folder1.getId())
      .markFolder(true)
      .build();
    FileV2 file2 = FileV2.builder()
      .id("file-2")
      .parentId(folder2.getId())
      .build();
    FileV2 file3 = FileV2.builder()
      .id("file-3")
      .parentId(folder2.getId())
      .build();
    
    when(fileProperties.getRootId()).thenReturn(ROOT);
    
    when(fileRepository.findByIdAndParentIdAndDeletedFalse(folder1.getId(),
                                                           folder1.getParentId()
    )).thenReturn(Optional.of(folder1));
    when(fileRepository.findAllByParentIdAndDeletedFalse(folder1.getId())).thenReturn(
      Arrays.asList(file1, folder2));
    
    when(fileRepository.findAllByParentIdAndDeletedFalse(file1.getId())).thenReturn(
      Collections.emptyList());
    when(fileRepository.findAllByParentIdAndDeletedFalse(folder2.getId())).thenReturn(
      Arrays.asList(file2, file3));
    when(fileRepository.findAllByParentIdAndDeletedFalse(file2.getId())).thenReturn(
      Collections.emptyList());
    when(fileRepository.findAllByParentIdAndDeletedFalse(file3.getId())).thenReturn(
      Collections.emptyList());
    
    List<String> fileIds = Arrays.asList(folder2.getId(), folder1.getId(),
                                         file1.getId(), file2.getId(),
                                         file3.getId()
    );
    when(resourceService.markFilesUsed(fileIds, false)).thenReturn(true);
    
    List<FileV2> fileV2s = Arrays.asList(folder2, folder1, file1, file2, file3);
    when(fileRepository.findAll(fileIds)).thenReturn(fileV2s);
    
    folder2.setDeleted(true);
    folder1.setDeleted(true);
    file1.setDeleted(true);
    file2.setDeleted(true);
    file3.setDeleted(true);
    when(fileRepository.save(fileV2s)).thenReturn(fileV2s);
    
    fileService.deleteFileOrFolder(SESSION, ROOT, folder1.getId());
    
    verify(fileProperties).getRootId();
    
    verify(fileRepository).findByIdAndParentIdAndDeletedFalse(
      folder1.getId(), folder1.getParentId());
    verify(fileRepository).findAllByParentIdAndDeletedFalse(folder1.getId());
    verify(fileRepository).findAllByParentIdAndDeletedFalse(file1.getId());
    verify(fileRepository).findAllByParentIdAndDeletedFalse(folder2.getId());
    verify(fileRepository).findAllByParentIdAndDeletedFalse(file2.getId());
    verify(fileRepository).findAllByParentIdAndDeletedFalse(file3.getId());
    
    verify(resourceService).markFilesUsed(fileIds, false);
    
    verify(fileRepository).findAll(fileIds);
    verify(fileRepository).save(fileV2s);
  }
  
  @Test
  public void testGivenInvalidEmailAndParentIdAndFileOrFolderIdByDeletingFileOrFolderReturnFailedDeletion() {
    
    when(fileProperties.getRootId()).thenReturn(ROOT);
    
    fileService.deleteFileOrFolder(SESSION, PARENT_ID, ROOT);
    
    verify(fileProperties).getRootId();
    verifyZeroInteractions(fileRepository, resourceService);
  }
  
  @Test
  public void testGivenMethodCallAndNonEmptyByteArrayByCreatingFileOrFolderReturnNewFile() {
    
    FileV2 returnedFile = FileV2.builder()
      .parentId(PARENT_ID)
      .name(NAME)
      .build();
    when(resourceService.storeFile(null, PARENT_ID, NAME, NAME, NAME.getBytes(),
                                   FileOrigin.FILE
    )).thenReturn(returnedFile);
    
    FileV2 savedFile = FileV2.builder()
      .parentId(PARENT_ID)
      .name(NAME)
      .used(true)
      .build();
    when(fileRepository.save(any(FileV2.class))).thenReturn(savedFile);
    
    FileV2 createdFile = fileService.createFileOrFolder(
      PARENT_ID, NAME, NAME, NAME.getBytes());
    
    assertThat(createdFile).isNotNull();
    assertThat(createdFile.getId()).isNotBlank();
    assertThat(createdFile.getName()).isEqualTo(NAME);
    assertThat(createdFile.getParentId()).isEqualTo(PARENT_ID);
    assertThat(createdFile.isUsed()).isTrue();
    assertThat(createdFile.isMarkFolder()).isFalse();
    
    verify(resourceService).storeFile(
      null, PARENT_ID, NAME, NAME, NAME.getBytes(), FileOrigin.FILE);
    verify(fileRepository).save(any(FileV2.class));
    verifyZeroInteractions(fileProperties);
  }
  
  @Test
  public void testGivenMethodCallAndEmptyByteArrayByCreatingFileOrFolderReturnNewFolder() {
    
    FileV2 returnedFolder = FileV2.builder()
      .name(NAME)
      .parentId(PARENT_ID)
      .used(true)
      .markFolder(true)
      .build();
    
    when(fileRepository.save(any(FileV2.class))).thenReturn(returnedFolder);
    
    FileV2 createdFolder = fileService.createFileOrFolder(PARENT_ID, NAME, NAME,
                                                          new byte[] {}
    );
    
    assertThat(createdFolder).isNotNull();
    assertThat(createdFolder.getId()).isNotBlank();
    assertThat(createdFolder.getName()).isEqualTo(NAME);
    assertThat(createdFolder.getParentId()).isEqualTo(PARENT_ID);
    assertThat(createdFolder.isUsed()).isTrue();
    assertThat(createdFolder.isMarkFolder()).isTrue();
    
    verify(fileRepository).save(any(FileV2.class));
    verifyZeroInteractions(resourceService, fileProperties);
  }
  
  @Test
  public void testGivenMethodCallAndNonEmptyByteArrayByUpdatingFileOrFolderReturnUpdatedFile() {
    
    when(fileRepository.findByIdAndParentIdAndDeletedFalse(ID, PARENT_ID)).thenReturn(
      Optional.of(file));
    when(resourceService.storeFile(ID, PARENT_ID, NAME, NAME, NAME.getBytes(),
                                   FileOrigin.FILE
    )).thenReturn(file);
    when(fileRepository.findOne(ID)).thenReturn(file);
    when(fileRepository.save(file)).thenReturn(file);
    
    FileV2 updatedFile = fileService.updateFileOrFolder(
      SESSION, ID, PARENT_ID, NAME, NAME, NAME.getBytes());
    
    assertThat(updatedFile).isNotNull();
    assertThat(updatedFile).isEqualTo(file);
    
    verify(fileRepository).findByIdAndParentIdAndDeletedFalse(ID, PARENT_ID);
    verify(resourceService).storeFile(
      ID, PARENT_ID, NAME, NAME, NAME.getBytes(), FileOrigin.FILE);
    verify(fileRepository).findOne(ID);
    verify(fileRepository).save(file);
    verifyZeroInteractions(fileProperties);
  }
  
  @Test
  public void testGivenMethodCallAndEmptyByteArrayByUpdatingFileOrFolderReturnUpdatedFolder() {
    
    FileV2 folder = FileV2.builder()
      .name(NAME)
      .parentId(PARENT_ID)
      .used(true)
      .markFolder(true)
      .build();
    
    when(
      fileRepository.findByIdAndParentIdAndDeletedFalse(folder.getId(), PARENT_ID)).thenReturn(
      Optional.of(folder));
    when(fileRepository.save(folder)).thenReturn(folder);
    
    FileV2 updatedFolder = fileService.updateFileOrFolder(
      SESSION, folder.getId(), PARENT_ID, NAME, NAME, new byte[] {});
    
    assertThat(updatedFolder).isNotNull();
    assertThat(updatedFolder).isEqualTo(folder);
    
    verify(fileRepository).findByIdAndParentIdAndDeletedFalse(folder.getId(), PARENT_ID);
    verify(fileRepository).save(folder);
    verifyZeroInteractions(resourceService, fileProperties);
  }
  
}
