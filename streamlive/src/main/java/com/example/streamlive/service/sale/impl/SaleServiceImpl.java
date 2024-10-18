package com.example.streamlive.service.sale.impl;

import com.example.streamlive.dao.product.ProductDao;
import com.example.streamlive.dao.sale.SaleDao;
import com.example.streamlive.dto.product.CheckOutDto;
import com.example.streamlive.model.order.Order;
import com.example.streamlive.model.order.OrderDetail;
import com.example.streamlive.service.sale.SaleService;
import com.example.streamlive.websocket.SignalingHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    @Value("${tappay.partner_key}")
    private String partnerKey;

    @Value("${tappay.merchant_id}")
    private String merchantId;

    private final ProductDao productDao;
    private final SaleDao saleDao;
    private final SignalingHandler signalingHandler;
    private final ObjectMapper objectMapper;

    public Boolean checkout(CheckOutDto checkOutDto){
        try {

            // 檢查庫存
            int currentStock = productDao.findProductStockById(checkOutDto.getProductId());
            int purchaseQuantity = checkOutDto.getQuantity();
            int newStock = currentStock - purchaseQuantity;

            if(currentStock < purchaseQuantity){
                log.error("檢查庫存 error");
                return false;
            }

            // 更新庫存
            Integer updateResult = productDao.updateProductStockById(checkOutDto.getProductId(), newStock);
            if (updateResult == null || updateResult == 0) {
                log.error("updateResult error");
                return false;
            }

            // 產生訂單
            int orderId = saleDao.createOrder(checkOutDto);

            // 寄件資訊
            int recipentId = saleDao.createRecipent(checkOutDto, orderId);
            //付款狀態
            if (tapPay(checkOutDto) != 0){
                log.error("pay failed");
                return false;
            }
            //更新付款狀態
            int updateStatus = saleDao.updateOrderStatus(orderId,1);
            if (updateStatus > 0) {

                String roomId = checkOutDto.getLiveId();
                int productId = checkOutDto.getProductId();
                int stock = productDao.findProductStockById(productId);

                JSONObject message = new JSONObject();
                message.put("type", "productSold");
                message.put("productId", productId);
                message.put("buyerName",checkOutDto.getName());
                message.put("newStock", stock);
                message.put("totalPrice", checkOutDto.getTotalPrice());
                message.put("productName",checkOutDto.getProductName());
                message.put("quantitySold", checkOutDto.getQuantity());

                signalingHandler.broadcastToViewers(roomId, message);

            }

            return true;
        } catch (Exception e) {
            log.error("checkout error", e.getMessage());
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public List<Order> getUserOrders(Long userId){
        return saleDao.getOrdersByUserId(userId);
    }

    @Override
    public OrderDetail getOrderDetail(Long orderId){
        return saleDao.getOrderDetailByOrderId(orderId);
    }

    public int tapPay(CheckOutDto checkOutDto) {
        String url = "https://sandbox.tappaysdk.com/tpc/payment/pay-by-prime";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("x-api-key", partnerKey);
        Map<String, Object> cardholder = new HashMap<>();
        cardholder.put("phone_number",checkOutDto.getRecipentDto().getPhone());
        cardholder.put("name",checkOutDto.getRecipentDto().getName());
        cardholder.put("email", checkOutDto.getRecipentDto().getEmail());
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prime", checkOutDto.getPrime());
        requestBody.put("partner_key", partnerKey);
        requestBody.put("merchant_id", merchantId);
        requestBody.put("amount", checkOutDto.getTotalPrice());
        requestBody.put("details", "Payment for order");
        requestBody.put("cardholder", cardholder);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        log.error(response.getBody().toString());
        return (Integer) response.getBody().get("status");
    }
}
