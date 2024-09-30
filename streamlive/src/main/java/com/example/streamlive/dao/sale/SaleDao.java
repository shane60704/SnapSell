package com.example.streamlive.dao.sale;

import com.example.streamlive.dto.product.CheckOutDto;
import com.example.streamlive.model.order.Order;
import com.example.streamlive.model.order.OrderDetail;

import java.util.List;

public interface SaleDao {
    int createOrder (CheckOutDto checkOutDto);
    int createRecipent(CheckOutDto checkOutDto, int orderId);
    int updateOrderStatus(int orderId, int status);
    List <Order> getOrdersByUserId(Long userId);
    OrderDetail getOrderDetailByOrderId(Long orderId);

}
