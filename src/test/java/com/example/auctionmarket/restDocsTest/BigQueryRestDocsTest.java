package com.example.auctionmarket.restDocsTest;

import com.example.auctionmarket.domain.analytics.controller.BigQueryAnalyticsController;
import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.service.BigQueryAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BigQueryAnalyticsController.class)
public class BigQueryRestDocsTest extends BaseRestDocsTest{

    @MockitoBean
    private BigQueryAnalyticsService bigQueryAnalyticsService;

    @Test
    void 시간별_평균_낙찰가_빅데이터_RestDocsAPI() throws Exception {

        List<HourlyAverageBidResponse> Response = List.of(
                new HourlyAverageBidResponse(0, 1000),
                new HourlyAverageBidResponse(1, 1200)
        );

        given(bigQueryAnalyticsService.getHourlyAverageBid(any()))
                .willReturn(Response);

        // when & then
        mockMvc.perform(get("/v1/analytics/bigquery/hourly-average-bid")
                        .param("category", "BAG")
                        .param("days", "7")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("hourly-average-bid",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data[].hourOfDay").description("시간별"),
                                fieldWithPath("data[].averageWinningBid").description("해당 시간대의 평균 낙찰가")
                        )
                ));
    }


    @Test
    void 일자별_평균_낙찰가_빅데이터() throws Exception {

        LocalDate auctionDate = LocalDate.of(2025, 5, 5);

        List<DailyAverageBidResponse> Response = List.of(
                new DailyAverageBidResponse(auctionDate, 1000),
                new DailyAverageBidResponse(auctionDate, 1200)
        );

        given(bigQueryAnalyticsService.getDailyAverageBidByCategory(any()))
                .willReturn(Response);

        // when & then
        mockMvc.perform(get("/v1/analytics/bigquery/daily-average-bid")
                        .param("category", "BAG")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("daily-average-bid",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data[].auctionDate").description("날짜별"),
                                fieldWithPath("data[].averageWinningBid").description("해당 시간대의 평균 낙찰가")
                        )
                ));
    }
}



