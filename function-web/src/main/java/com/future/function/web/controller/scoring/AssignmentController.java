package com.future.function.web.controller.scoring;

import com.future.function.service.api.feature.scoring.AssignmentService;
import com.future.function.web.mapper.helper.PageHelper;
import com.future.function.web.mapper.helper.ResponseHelper;
import com.future.function.web.mapper.request.scoring.AssignmentRequestMapper;
import com.future.function.web.mapper.response.scoring.AssignmentResponseMapper;
import com.future.function.web.model.request.scoring.AssignmentWebRequest;
import com.future.function.web.model.request.scoring.CopyAssignmentWebRequest;
import com.future.function.web.model.response.base.BaseResponse;
import com.future.function.web.model.response.base.DataResponse;
import com.future.function.web.model.response.base.PagingResponse;
import com.future.function.web.model.response.feature.scoring.AssignmentWebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class used to serve and received data from the Web in manipulation of Assignment Entity
 */
@RestController
@RequestMapping(value = "/api/scoring/batches/{batchCode}/assignments")
public class AssignmentController {

  private AssignmentService assignmentService;

  private AssignmentRequestMapper assignmentRequestMapper;

  @Autowired
  public AssignmentController(AssignmentService assignmentService, AssignmentRequestMapper assignmentRequestMapper) {
    this.assignmentService = assignmentService;
    this.assignmentRequestMapper = assignmentRequestMapper;
  }

  /**
   * Used to retrieve List of Assignment with Paging, Filtering, And Search Keyword
   *
   * @param page   (Int)
   * @param size   (Int)
   * @return PagingResponse<AssignmentWebResponse> contains List of Assignment and the Paging Information
   */
  @ResponseStatus(value = HttpStatus.OK)
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public PagingResponse<AssignmentWebResponse> findAllAssignment(
          @PathVariable(value = "batchCode") String batchCode,
          @RequestParam(required = false, defaultValue = "1") int page,
          @RequestParam(required = false, defaultValue = "10") int size
  ) {
    return AssignmentResponseMapper
            .toAssignmentsPagingResponse(
                    assignmentService.findAllByBatchCodeAndPageable(batchCode, PageHelper.toPageable(page, size)));
  }

  /**
   * Used to retrieve specific Assignment Object By Passing the Assignment Id In PathVariable
   *
   * @param id (String)
   * @return DataResponse<AssignmentWebResponse> contains the specific Assignment Object
   */
  @ResponseStatus(value = HttpStatus.OK)
  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public DataResponse<AssignmentWebResponse> findAssignmentById(@PathVariable String id) {
      return AssignmentResponseMapper.toAssignmentDataResponse(assignmentService.findById(id));
  }

  /**
   * Used to create new {@code Assignment) by passing the JSON containing Assignment Attributes and Uploaded File
   *
   * @param data (JSON)
   * @param file (MultipartFile) (Not Required)
   * @return DataResponse<AssignmentWebResponse> containing created Assignment
   */
  @ResponseStatus(value = HttpStatus.CREATED)
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public DataResponse<AssignmentWebResponse> createAssignment(@RequestBody AssignmentWebRequest data) {
    return AssignmentResponseMapper
            .toAssignmentDataResponse(HttpStatus.CREATED, assignmentService
                    .createAssignment(assignmentRequestMapper.toAssignment(data)));
  }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path = "/copy", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AssignmentWebResponse> copyAssignment(@RequestBody CopyAssignmentWebRequest request) {
        return AssignmentResponseMapper
                .toAssignmentDataResponse(HttpStatus.CREATED, assignmentService
                        .copyAssignment(request.getAssignmentId(), request.getTargetBatch()));
  }

  /**
   * Used to update existing Assignment By Passing the id, JSON containing Assignment attributes, and Uploaded File
   *
   * @param data (JSON)
   * @return DataResponse<AssignmentWebResponse> containing updated Assignment
   */
  @ResponseStatus(value = HttpStatus.OK)
  @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public DataResponse<AssignmentWebResponse> updateAssignment(@PathVariable String id,
                                                              @RequestBody AssignmentWebRequest data) {
      return AssignmentResponseMapper.toAssignmentDataResponse(assignmentService
              .updateAssignment(assignmentRequestMapper.toAssignmentWithId(id, data)));
  }

  /**
   * Used to delete specific Assignment by Passing Assignment Id in PathVariable
   *
   * @param id (JSON)
   * @return BaseResponse with status OK
   */
  @ResponseStatus(value = HttpStatus.OK)
  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public BaseResponse deleteAssignmentById(@PathVariable String id) {
    assignmentService.deleteById(id);
      return ResponseHelper.toBaseResponse(HttpStatus.OK);
  }

}
