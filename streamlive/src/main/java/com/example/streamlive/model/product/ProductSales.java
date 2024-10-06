package com.example.streamlive.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSales {
    Long productId;
    Long totalPrice;
    Long totalQuantity;
    int delegationId;
    String commissionRate;
}
