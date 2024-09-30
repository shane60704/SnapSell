package com.example.streamlive.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutDto {
    private String prime;
    // 1. user_Id 買家編號
    private int userId;
    // 2. product_Id 商品編號
    private int productId;
    // 3, live_id 直播編號
    private String liveId;
    // 4. quantity 數量
    private int quantity;

    private int totalPrice;
    // 5. freight 運費
    private int freight;
    // 6. 下單時間
    private String orderTime;
    // 7. 收件者資訊
    private RecipentDto recipentDto;
}
