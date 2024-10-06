package com.example.streamlive.service.livestream;

import java.util.List;
import java.util.Map;

public interface LiveStreamService {
    Map<String, Object> getLiveStreamSummary(Long userid);

    Map<String, Object> getLiveStreamStatistics(String liveId);

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
