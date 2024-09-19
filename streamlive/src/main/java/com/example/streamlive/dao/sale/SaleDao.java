package com.example.streamlive.dao.sale;

import com.example.streamlive.dto.product.CheckOutDto;

public interface SaleDao {
    int createOrder (CheckOutDto checkOutDto);
    int createRecipent(CheckOutDto checkOutDto, int orderId);
    int updateOrderStatus(int orderId, int status);
}
