package com.example.streamlive.service.sale.impl;

import com.example.streamlive.dao.product.ProductDao;
import com.example.streamlive.dao.sale.SaleDao;
import com.example.streamlive.dto.product.CheckOutDto;
import com.example.streamlive.exception.custom.*;
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

    @Override
    public Boolean checkout(CheckOutDto checkOutDto) {
        // 檢查庫存
        if (!checkStock(checkOutDto)) {
            throw new InsufficientStockException("庫存不足");
        }

        // 更新庫存
        if (!updateStock(checkOutDto)) {
            throw new StockUpdateFailedException("更新庫存失敗");
        }

        // 產生訂單與收件人資訊
        int orderId = createOrder(checkOutDto);
        createRecipient(checkOutDto, orderId);

        // 處理支付
        if (!processPayment(checkOutDto)) {
            throw new PaymentProcessingException("支付失敗");
        }

        // 更新訂單狀態
        saleDao.updateOrderStatus(orderId, 1);

        // 廣播訊息
        broadcastMessage(checkOutDto, orderId);

        return true;
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

    private boolean checkStock(CheckOutDto checkOutDto) {
        int currentStock = productDao.findProductStockById(checkOutDto.getProductId());
        int purchaseQuantity = checkOutDto.getQuantity();

        if (currentStock < purchaseQuantity) {
            log.error("庫存不足: currentStock = {}, purchaseQuantity = {}", currentStock, purchaseQuantity);
            return false;
        }

        return true;
    }

    private boolean updateStock(CheckOutDto checkOutDto) {
        int currentStock = productDao.findProductStockById(checkOutDto.getProductId());
        int newStock = currentStock - checkOutDto.getQuantity();
        Integer updateResult = productDao.updateProductStockById(checkOutDto.getProductId(), newStock);

        if (updateResult == null || updateResult == 0) {
            log.error("更新庫存失敗");
            return false;
        }

        return true;
    }

    private int createOrder(CheckOutDto checkOutDto) {
        int orderId = saleDao.createOrder(checkOutDto);
        if (orderId <= 0) {
            log.error("訂單創建失敗");
            throw new OrderCreationFailedException("訂單創建失敗");
        }
        return orderId;
    }

    private void createRecipient(CheckOutDto checkOutDto, int orderId) {
        int recipientId = saleDao.createRecipent(checkOutDto, orderId);
        if (recipientId <= 0) {
            log.error("收件人創建失敗");
            throw new RecipientCreationFailedException("收件人創建失敗");
        }
    }

    private boolean processPayment(CheckOutDto checkOutDto) {
        if (tapPay(checkOutDto) != 0) {
            log.error("支付失敗");
            throw new PaymentProcessingException("支付失敗");
        }
        return true;
    }

    private void broadcastMessage(CheckOutDto checkOutDto, int orderId) {
        String roomId = checkOutDto.getLiveId();
        int productId = checkOutDto.getProductId();
        int stock = productDao.findProductStockById(productId);

        JSONObject message = new JSONObject();
        message.put("type", "productSold");
        message.put("productId", productId);
        message.put("buyerName", checkOutDto.getName());
        message.put("newStock", stock);
        message.put("totalPrice", checkOutDto.getTotalPrice());
        message.put("productName", checkOutDto.getProductName());
        message.put("quantitySold", checkOutDto.getQuantity());

        signalingHandler.broadcastToViewers(roomId, message);
    }

}
