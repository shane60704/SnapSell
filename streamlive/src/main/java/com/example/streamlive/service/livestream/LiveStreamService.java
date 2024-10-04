package com.example.streamlive.service.livestream;

import java.util.Map;

public interface LiveStreamService {
    Map<String,Object> getLiveStreamSummary(Long userid);
}
