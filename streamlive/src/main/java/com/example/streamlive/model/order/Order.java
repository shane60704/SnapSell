package com.example.streamlive.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private String orderTime;
    private Long totalPrice;
    private String logisticsStatus;
}
