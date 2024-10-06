package com.example.streamlive.service.livestream.impl;

import com.example.streamlive.dao.livestream.LiveStreamDao;
import com.example.streamlive.model.DelegationDetails;
import com.example.streamlive.model.livestream.LiveStreamRecord;
import com.example.streamlive.model.livestream.LiveSummary;
import com.example.streamlive.model.livestream.Satisfaction;
import com.example.streamlive.model.product.ProductSales;
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
    public Map<String, Object> getLiveStreamSummary(Long userid) {
        Map<String, Object> map = new HashMap<>();
        LiveSummary liveSummary = liveStreamDao.findLiveSummaryByUserId(userid);
        List<Satisfaction> satisfactions = liveStreamDao.findSatisfactionRecordByUserId(userid);
        map.put("liveSummary", liveSummary);
        map.put("satisfactions", satisfactions);
        return map;
    }

    @Override
    public Map<String, Object> getLiveStreamStatistics(String liveId) {
        Map<String, Object> map = new HashMap<>();
        LiveStreamRecord liveStreamRecord = liveStreamDao.getSaleRecordsByLiveId(liveId);
        List<ProductSales> productSales = liveStreamDao.getProductSalesByLiveId(liveId);
        for (int i = 0; i < productSales.size(); i++) {
            DelegationDetails delegationDetails = liveStreamDao.getDelegationDetailsByProductIdAndAgentId(productSales.get(i).getProductId(), liveStreamRecord.getUserId());
            productSales.get(i).setDelegationId(delegationDetails.getDelegationId());
            productSales.get(i).setCommissionRate(delegationDetails.getCommissionRate());
        }
        map.put("liveStreamRecord", liveStreamRecord);
        map.put("productSales", productSales);
        return map;
    }

    @Override
    public List<Map<String, Object>> getTotalViewersByYear(Long userId) {
        return liveStreamDao.getTotalViewersByYear(userId);
    }

    @Override
    public List<Map<String, Object>> getTotalViewersByMonth(Long userId) {
        return liveStreamDao.getTotalViewersByMonth(userId);
    }

    @Override
    public List<Map<String, Object>> getTotalViewersByWeek(Long userId) {
        return liveStreamDao.getTotalViewersByWeek(userId);
    }

    @Override
    public List<Map<String, Object>> getTotalViewersByDay(Long userId) {
        return liveStreamDao.getTotalViewersByDay(userId);
    }

    @Override
    public List<Map<String, Object>> getTotalSalesByYear(Long userId) {
        return liveStreamDao.getTotalSalesByYear(userId);
    }

    @Override
    public List<Map<String, Object>> getTotalSalesByMonth(Long userId) {
        return liveStreamDao.getTotalSalesByMonth(userId);
    }

    @Override
    public List<Map<String, Object>> getTotalSalesByWeek(Long userId) {
        return liveStreamDao.getTotalSalesByWeek(userId);
    }

    @Override
    public List<Map<String, Object>> getTotalSalesByDay(Long userId) {
        return liveStreamDao.getTotalSalesByDay(userId);
    }

    @Override
    public List<Map<String, Object>> getTotalFiguresByYear(long userId) {
        return liveStreamDao.getTotalFiguresByYear(userId);
    }

    @Override
    public List<Map<String, Object>> getTotalFiguresByMonth(long userId) {
        return liveStreamDao.getTotalFiguresByMonth(userId);
    }

    @Override
    public List<Map<String, Object>> getTotalFiguresByWeek(long userId) {
        return liveStreamDao.getTotalFiguresByWeek(userId);
    }

    @Override
    public List<Map<String, Object>> getTotalFiguresByDay(long userId) {
        return liveStreamDao.getTotalFiguresByDay(userId);
    }

    @Override
    public List<Map<String, Object>> getCommissionByYear(Long userId) {
        return liveStreamDao.getCommissionByYear(userId);
    }

    @Override
    public List<Map<String, Object>> getCommissionByMonth(Long userId) {
        return liveStreamDao.getCommissionByMonth(userId);
    }

    @Override
    public List<Map<String, Object>> getCommissionByWeek(Long userId) {
        return liveStreamDao.getCommissionByWeek(userId);
    }

    @Override
    public List<Map<String, Object>> getCommissionByDay(Long userId) {
        return liveStreamDao.getCommissionByDay(userId);
    }

}