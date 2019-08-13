package com.future.function.service.impl.feature.scoring;

import com.future.function.common.exception.NotFoundException;
import com.future.function.model.entity.feature.core.Batch;
import com.future.function.model.entity.feature.core.User;
import com.future.function.model.entity.feature.scoring.Report;
import com.future.function.model.entity.feature.scoring.ReportDetail;
import com.future.function.model.util.constant.FieldName;
import com.future.function.model.vo.scoring.StudentSummaryVO;
import com.future.function.repository.feature.scoring.ReportRepository;
import com.future.function.service.api.feature.core.BatchService;
import com.future.function.service.api.feature.core.UserService;
import com.future.function.service.api.feature.scoring.ReportDetailService;
import com.future.function.service.api.feature.scoring.ReportService;
import com.future.function.service.impl.helper.CopyHelper;
import com.future.function.service.impl.helper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

  private ReportRepository reportRepository;

  private ReportDetailService reportDetailService;

  private UserService userService;

  private BatchService batchService;

  @Autowired
  public ReportServiceImpl(
    ReportRepository reportRepository, ReportDetailService reportDetailService,
    UserService userService, BatchService batchService
  ) {

    this.reportRepository = reportRepository;
    this.reportDetailService = reportDetailService;
    this.userService = userService;
    this.batchService = batchService;
  }

  @Override
  public Page<Report> findAllReport(String batchCode, Pageable pageable) {

    return Optional.ofNullable(batchCode)
      .map(batchService::getBatchByCode)
      .map(batch -> reportRepository.findAllByBatchAndDeletedFalse(batch,
                                                                   pageable
      ))
      .map(this::mapReportPageToHaveStudentIds)
      .orElseGet(() -> PageHelper.empty(pageable));
  }

  private Page<Report> mapReportPageToHaveStudentIds(Page<Report> reportPage) {

    reportPage.getContent()
      .forEach(this::findStudentIdsByReportId);
    return reportPage;
  }

  private Report findStudentIdsByReportId(Report report) {

    List<User> students = reportDetailService.findAllDetailByReportId(
      report.getId())
      .stream()
      .map(ReportDetail::getUser)
      .collect(Collectors.toList());
    report.setStudents(students);
    return report;
  }

  @Override
  public Report findById(String id) {

    return Optional.ofNullable(id)
      .flatMap(reportRepository::findByIdAndDeletedFalse)
      .map(this::findStudentIdsByReportId)
      .map(this::findStudentsAndSetReport)
      .orElseThrow(
        () -> new NotFoundException("Failed at #findById #ReportService"));
  }

  @Override
  public Report createReport(Report report) {

    List<User> students = report.getStudents();
    return Optional.ofNullable(report)
      .map(this::setBatch)
      .map(
        currentReport -> createReportDetailByReportAndStudentId(currentReport,
                                                                report.getStudents()
        ))
      .map(currentReport -> this.setStudents(currentReport, null))
      .map(reportRepository::save)
      .map(currentReport -> this.setStudents(currentReport, students))
      .map(this::findStudentsAndSetReport)
      .orElseThrow(() -> new UnsupportedOperationException(
        "Failed at #createReport #ReportService"));
  }

  private Report setBatch(Report value) {

    Batch batch = batchService.getBatchByCode(value.getBatch()
                                                .getCode());
    value.setBatch(batch);
    return value;
  }

  private Report createReportDetailByReportAndStudentId(
    Report report, List<User> students
  ) {

    return Optional.ofNullable(students)
      .map(studentList -> createDetailsFromStudentIds(report, studentList))
      .map(currentReport -> this.setStudents(currentReport, students))
      .map(this::findStudentsAndSetReport)
      .orElse(null);
  }

  private Report findStudentsAndSetReport(Report report) {

    List<User> students = report.getStudents()
      .stream()
      .map(User::getId)
      .map(userService::getUser)
      .collect(Collectors.toList());
    report.setStudents(students);
    return report;
  }

  private Report setStudents(Report report, List<User> students) {

    report.setStudents(students);
    return report;
  }

  private Report createDetailsFromStudentIds(
    Report report, List<User> students
  ) {

    return students.stream()
      .map(User::getId)
      .map(userService::getUser)
      .map(student -> reportDetailService.createReportDetailByReport(report,
                                                                     student
      ))
      .collect(Collectors.toList())
      .get(0);
  }

  @Override
  public Report updateReport(Report report) {

    List<User> students = report.getStudents();
    return Optional.ofNullable(report)
      .map(this::checkStudentIdsChangedAndDeleteIfChanged)
      .map(Report::getId)
      .map(this::findById)
      .map(
        foundReport -> this.copyReportRequestAttributesIgnoreBatchField(report,
                                                                        foundReport
        ))
      .map(foundReport -> this.setStudents(foundReport, null))
      .map(reportRepository::save)
      .map(foundReport -> this.setStudents(foundReport, students))
      .map(this::findStudentsAndSetReport)
      .orElse(report);
  }

  private Report copyReportRequestAttributesIgnoreBatchField(
    Report request, Report report
  ) {

    CopyHelper.copyProperties(request, report, FieldName.Report.BATCH);
    return report;
  }

  private Report checkStudentIdsChangedAndDeleteIfChanged(Report report) {

    return Optional.ofNullable(report)
      .filter(this::isStudentListChangedFromRepository)
      .orElseGet(() -> this.deleteAllDetailByReportId(report));
  }

  private boolean isStudentListChangedFromRepository(Report report) {

    List<String> studentIds = report.getStudents()
      .stream()
      .map(User::getId)
      .collect(Collectors.toList());
    return reportDetailService.findAllDetailByReportId(report.getId())
      .stream()
      .map(ReportDetail::getUser)
      .map(User::getId)
      .collect(Collectors.toList())
      .containsAll(studentIds);
  }

  private Report deleteAllDetailByReportId(Report report) {

    return Optional.ofNullable(report)
      .map(this::deleteExistingDetailAndCreateNew)
      .orElse(report);
  }

  private Report deleteExistingDetailAndCreateNew(Report currentReport) {

    reportDetailService.deleteAllByReportId(currentReport.getId());
    return this.createReportDetailByReportAndStudentId(
      currentReport, currentReport.getStudents());
  }

  @Override
  public void deleteById(String id) {

    Optional.ofNullable(id)
      .flatMap(reportRepository::findByIdAndDeletedFalse)
      .ifPresent(this::setDetailsAsDeletedAndSave);
  }

  @Override
  public List<StudentSummaryVO> findAllSummaryByReportId(
    String reportId, String userId
  ) {

    return reportDetailService.findAllSummaryByReportId(reportId, userId);
  }

  @Override
  public Page<Pair<User, Integer>> findAllStudentsAndFinalPointByBatch(
    String batchCode, Pageable pageable
  ) {

    Page<User> userPage = userService.getStudentsWithinBatch(
      batchCode, pageable);
    return userPage.getContent()
      .stream()
      .map(this::findFinalPointAndMapToPair)
      .collect(Collectors.collectingAndThen(Collectors.toList(),
                                            pairList -> new PageImpl<>(pairList,
                                                                       pageable,
                                                                       userPage.getTotalElements()
                                            )
      ));
  }

  private Pair<User, Integer> findFinalPointAndMapToPair(User user) {

    Integer totalPoint = getFinalPointFromNullableReportDetail(
      reportDetailService.findByStudentId(user.getId(), user.getId()));
    return Pair.of(user, totalPoint);
  }

  private Integer getFinalPointFromNullableReportDetail(
    ReportDetail reportDetail
  ) {

    return Optional.ofNullable(reportDetail)
      .map(ReportDetail::getPoint)
      .orElse(0);
  }

  @Override
  public List<ReportDetail> giveScoreToReportStudents(
    String reportId, List<ReportDetail> reportDetailList
  ) {

    return Optional.ofNullable(reportId)
      .flatMap(reportRepository::findByIdAndDeletedFalse)
      .map(report -> reportDetailService.giveScoreToEachStudentInDetail(report,
                                                                        reportDetailList
      ))
      .orElseThrow(() -> new UnsupportedOperationException(
        "Failed at #giveScoreToReportStudents"));
  }

  private void setDetailsAsDeletedAndSave(Report report) {

    reportDetailService.deleteAllByReportId(report.getId());
    report.setDeleted(true);
    reportRepository.save(report);
  }

}
