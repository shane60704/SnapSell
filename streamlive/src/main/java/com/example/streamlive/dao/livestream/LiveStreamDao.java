package com.example.streamlive.dao.livestream;

import com.example.streamlive.dto.SatisfactionDto;
import com.example.streamlive.model.DelegationDetails;
import com.example.streamlive.model.livestream.LiveStreamRecord;
import com.example.streamlive.model.livestream.LiveSummary;
import com.example.streamlive.model.livestream.Satisfaction;
import com.example.streamlive.model.product.ProductSales;

import java.util.List;
import java.util.Map;

public interface LiveStreamDao {
    Long createLiveStreamRecord(String userId, String liveId);

    void updateLiveStreamRecord(String liveId, int viewers, int totalQuantity, int totalFigures);

    Map<String, Object> getTotalPriceAndQuantity(String liveId);

    List<LiveStreamRecord> findLiveStreamRecordsByUserId(int userId);

    Integer findLiveRecordIdByLiveId(String liveId);

    void saveSatisfactionRecord(SatisfactionDto satisfactionDto, Integer liveRecordId);

    List<Satisfaction> findSatisfactionRecordByLiveId(Long liveId);

    List<Satisfaction> findSatisfactionRecordByUserId(Long userId);

    LiveSummary findLiveSummaryByUserId(Long userId);

    LiveStreamRecord getSaleRecordsByLiveId(String liveId);

    List<ProductSales> getProductSalesByLiveId(String liveId);

    DelegationDetails getDelegationDetailsByProductIdAndAgentId(Long productId, int agentId);

    List<Map<String, Object>> getTotalViewersByYear(Long userId);

    List<Map<String, Object>> getTotalViewersByMonth(Long userId);

    List<Map<String, Object>> getTotalViewersByWeek(Long userId);

    List<Map<String, Object>> getTotalViewersByDay(Long userId);

    List<Map<String, Object>> getTotalSalesByYear(Long userId);

    List<Map<String, Object>> getTotalSalesByMonth(Long userId);

    List<Map<String, Object>> getTotalSalesByWeek(Long userId);

    List<Map<String, Object>> getTotalSalesByDay(Long userId);

    List<Map<String, Object>> getTotalFiguresByYear(long userId);

    List<Map<String, Object>> getTotalFiguresByMonth(long userId);

    List<Map<String, Object>> getTotalFiguresByWeek(long userId);

    List<Map<String, Object>> getTotalFiguresByDay(long userId);

    List<Map<String, Object>> getCommissionByYear(Long userId);

    List<Map<String, Object>> getCommissionByMonth(Long userId);

    List<Map<String, Object>> getCommissionByWeek(Long userId);

    List<Map<String, Object>> getCommissionByDay(Long userId);
}
