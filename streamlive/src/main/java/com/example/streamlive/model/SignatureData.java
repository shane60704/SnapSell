package com.example.streamlive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignatureData {
    private String requestType;
    private String contractId;  // 合約ID
    private String userId;      // 簽名者ID
    private String roomId;      // 房間ID，用來標識特定的聊天室
    private String signatureData; // 簽名的圖像數據，以Base64格式存儲
    private String chatRoomId; // 當前聊天室 ID
    private String senderId; // 發送訊息者 ID
    private String clientName;
    private String clientId;
    private String agentId;
    private String sellerName;
    private String productName;
    private String productId;
    private String salesPeriod;
    private String commissionRate;
    private String signatureImage;
    private String agentSignatureImage;
    private String timestamp;
}
