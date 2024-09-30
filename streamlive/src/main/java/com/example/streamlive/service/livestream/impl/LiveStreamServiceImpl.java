package com.example.streamlive.service.livestream.impl;

import com.example.streamlive.dao.livestream.LiveStreamDao;
import com.example.streamlive.service.livestream.LiveStreamService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LiveStreamServiceImpl implements LiveStreamService {

    private final LiveStreamDao liveStreamDao;


}
