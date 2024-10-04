package com.example.streamlive.service.livestream.impl;

import com.example.streamlive.dao.livestream.LiveStreamDao;
import com.example.streamlive.model.livestream.LiveSummary;
import com.example.streamlive.model.livestream.Satisfaction;
import com.example.streamlive.service.livestream.LiveStreamService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class LiveStreamServiceImpl implements LiveStreamService {

    private final LiveStreamDao liveStreamDao;

    @Override
    public Map<String,Object> getLiveStreamSummary(Long userid){
        Map<String,Object> map = new HashMap<>();
        LiveSummary liveSummary = liveStreamDao.findLiveSummaryByUserId(userid);
        List<Satisfaction> satisfactions = liveStreamDao.findSatisfactionRecordByUserId(userid);
        map.put("liveSummary",liveSummary);
        map.put("satisfactions",satisfactions);
        return map;
    }
}
