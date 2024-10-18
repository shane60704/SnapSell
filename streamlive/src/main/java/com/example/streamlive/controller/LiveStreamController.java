package com.example.streamlive.controller;

import com.example.streamlive.dao.livestream.LiveStreamDao;
import com.example.streamlive.dto.SatisfactionDto;
import com.example.streamlive.dto.response.APIResponse;
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
        return ResponseEntity.ok(new APIResponse<>(liveStreamDao.findLiveStreamRecordsByUserId(userId)));
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveSatisfaction(@RequestBody SatisfactionDto satisfactionDto) {
        Integer liveRecordId = liveStreamDao.findLiveRecordIdByLiveId(satisfactionDto.getLiveId());
        liveStreamDao.saveSatisfactionRecord(satisfactionDto, liveRecordId);
        return ResponseEntity.ok(new APIResponse<>("Success"));
    }

    @GetMapping("/{liveId}/satisfaction")
    public ResponseEntity<?> getSatisfaction(@PathVariable Long liveId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamDao.findSatisfactionRecordByLiveId(liveId)));
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<?> getLiveStreamSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getLiveStreamSummary(userId)));
    }

    @GetMapping("/{liveId}/summary-records")
    public ResponseEntity<?> getLiveStreamStatistics(@PathVariable String liveId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getLiveStreamStatistics(liveId)));
    }

    @GetMapping("/yearly-viewers/{userId}")
    public ResponseEntity<?> getYearlyViewers(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalViewersByYear(userId)));
    }

    @GetMapping("/monthly-viewers/{userId}")
    public ResponseEntity<?> getMonthlyViewers(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalViewersByMonth(userId)));
    }

    @GetMapping("/weekly-viewers/{userId}")
    public ResponseEntity<?> getWeeklyViewers(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalViewersByWeek(userId)));
    }

    @GetMapping("/daily-viewers/{userId}")
    public ResponseEntity<?> getDailyViewers(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalViewersByDay(userId)));
    }

    @GetMapping("/yearly-sales/{userId}")
    public ResponseEntity<?> getYearlySales(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalSalesByYear(userId)));
    }

    @GetMapping("/monthly-sales/{userId}")
    public ResponseEntity<?> getMonthlySales(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalSalesByMonth(userId)));
    }

    @GetMapping("/weekly-sales/{userId}")
    public ResponseEntity<?> getWeeklySales(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalSalesByWeek(userId)));
    }

    @GetMapping("/daily-sales/{userId}")
    public ResponseEntity<?> getDailySales(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalSalesByDay(userId)));
    }

    @GetMapping("/yearly-figures/{userId}")
    public ResponseEntity<?> getYearlyFigures(@PathVariable long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalFiguresByYear(userId)));
    }

    @GetMapping("/monthly-figures/{userId}")
    public ResponseEntity<?> getMonthlyFigures(@PathVariable long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalFiguresByMonth(userId)));
    }

    @GetMapping("/weekly-figures/{userId}")
    public ResponseEntity<?> getWeeklyFigures(@PathVariable long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalFiguresByWeek(userId)));
    }

    @GetMapping("/daily-figures/{userId}")
    public ResponseEntity<?> getDailyFigures(@PathVariable long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getTotalFiguresByDay(userId)));
    }

    @GetMapping("/yearly-commissions/{userId}")
    public ResponseEntity<?> getYearlyCommission(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getCommissionByYear(userId)));
    }

    @GetMapping("/monthly-commissions/{userId}")
    public ResponseEntity<?> getMonthlyCommission(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getCommissionByMonth(userId)));
    }

    @GetMapping("/weekly-commissions/{userId}")
    public ResponseEntity<?> getWeeklyCommission(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getCommissionByWeek(userId)));
    }

    @GetMapping("/daily-commissions/{userId}")
    public ResponseEntity<?> getDailyCommission(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(liveStreamService.getCommissionByDay(userId)));
    }
}
