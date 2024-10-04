package com.example.streamlive.controller;

import com.example.streamlive.dao.livestream.LiveStreamDao;
import com.example.streamlive.dto.SatisfactionDto;
import com.example.streamlive.dto.response.ApiResponse;
import com.example.streamlive.service.livestream.LiveStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/livestream")
public class LiveStreamController {

    private final LiveStreamDao liveStreamDao;
    private final LiveStreamService liveStreamService;

    @GetMapping("/user/{userId}/records")
    public ResponseEntity<?> getLiveStreamRecordsByUserId(@PathVariable int userId) {
        return ResponseEntity.ok(new ApiResponse<>(liveStreamDao.findLiveStreamRecordsByUserId(userId)));
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveSatisfaction(@RequestBody SatisfactionDto satisfactionDto){
        Integer liveRecordId = liveStreamDao.findLiveRecordIdByLiveId(satisfactionDto.getLiveId());
        liveStreamDao.saveSatisfactionRecord(satisfactionDto,liveRecordId);
        return ResponseEntity.ok(new ApiResponse<>("Success"));
    }

    @GetMapping("/{liveId}/satisfaction")
    public ResponseEntity<?> getSatisfaction(@PathVariable Long liveId){
        return ResponseEntity.ok(new ApiResponse<>(liveStreamDao.findSatisfactionRecordByLiveId(liveId)));
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<?> getLiveStreamSummary(@PathVariable Long userId){
        return ResponseEntity.ok(new ApiResponse<>(liveStreamService.getLiveStreamSummary(userId)));
    }


}
