package com.example.streamlive.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutDto {
    private String prime;

    private String name;

    private String productName;

    private int userId;

    private int productId;

    private String liveId;

    private int quantity;

    private int totalPrice;

    private int freight;

    private String orderTime;

    private RecipentDto recipentDto;
}
