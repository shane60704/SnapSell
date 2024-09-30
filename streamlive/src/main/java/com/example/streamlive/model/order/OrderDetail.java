package com.example.streamlive.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
    private int orderId;
    private String userName;
    private String productName;
    private int productPrice;
    private int quantity;
    private int totalPrice;
    private String orderTime;
    private String recipientName;
    private String recipientPhone;
    private String recipientEmail;
    private String recipientAddress;
}
