package com.future.function.web.mapper.response.scoring;

import com.future.function.model.dto.scoring.StudentSummaryDTO;
import com.future.function.model.entity.feature.scoring.ReportDetail;
import com.future.function.web.mapper.helper.ResponseHelper;
import com.future.function.web.model.response.base.DataResponse;
import com.future.function.web.model.response.feature.scoring.ReportDetailWebResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportDetailResponseMapper {

  public static DataResponse<ReportDetailWebResponse> toDataReportDetailWebResponse(StudentSummaryDTO summaryDTO) {
    return ResponseHelper.toDataResponse(HttpStatus.OK, buildReportDetailWebResponse(summaryDTO));
    }

    public static DataResponse<List<ReportDetailWebResponse>> toDataListReportDetailWebResponseFromReportDetail(
            HttpStatus httpStatus, List<ReportDetail> reportDetailList) {
        return ResponseHelper.toDataResponse(httpStatus, buildListFromReportDetailList(reportDetailList));
    }

  private static ReportDetailWebResponse buildReportDetailWebResponse(StudentSummaryDTO summaryDTO) {
        return ReportDetailWebResponse.builder()
            .studentName(summaryDTO.getStudentName())
            .batchCode(summaryDTO.getBatchCode())
            .university(summaryDTO.getUniversity())
                .avatar(summaryDTO.getAvatar())
            .scores(ScoreSummaryResponseMapper.toDataListSummaryResponse(summaryDTO.getScores()).getData())
                .build();
    }

    private static List<ReportDetailWebResponse> buildListOfSummaryDTOs(List<StudentSummaryDTO> summaryDTOs) {
    return summaryDTOs.stream()
        .map(ReportDetailResponseMapper::buildReportDetailWebResponse)
                .collect(Collectors.toList());
    }

    private static ReportDetailWebResponse buildReportDetailWebResponse(ReportDetail reportDetail) {
        return ReportDetailWebResponse.builder()
                .studentName(reportDetail.getUser().getName())
                .batchCode(reportDetail.getUser().getBatch().getCode())
                .university(reportDetail.getUser().getUniversity())
                .avatar(reportDetail.getUser().getPictureV2().getFileUrl())
                .point(reportDetail.getPoint())
                .build();
    }

    private static List<ReportDetailWebResponse> buildListFromReportDetailList(List<ReportDetail> reportDetails) {
        return reportDetails.stream()
                .map(ReportDetailResponseMapper::buildReportDetailWebResponse)
                .collect(Collectors.toList());
    }

  public static DataResponse<List<ReportDetailWebResponse>> toDataListReportDetailWebResponse(List<StudentSummaryDTO> summaryDTOs) {
      return ResponseHelper.toDataResponse(HttpStatus.OK, buildListOfSummaryDTOs(summaryDTOs));
    }

}
