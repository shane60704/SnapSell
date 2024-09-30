package com.example.streamlive.dao.livestream;

import com.example.streamlive.dto.SatisfactionDto;
import com.example.streamlive.model.livestream.LiveStreamRecord;

import java.util.List;
import java.util.Map;

public interface LiveStreamDao {
    Long createLiveStreamRecord(String userId, String liveId);
    void updateLiveStreamRecord(String liveId,int viewers,int totalQuantity,int totalFigures);
    Map<String, Object> getTotalPriceAndQuantity(String liveId);
    List<LiveStreamRecord> findLiveStreamRecordsByUserId(int userId);
    Integer findLiveRecordIdByLiveId(String liveId);
    void saveSatisfactionRecord(SatisfactionDto satisfactionDto, Integer liveRecordId);
}
