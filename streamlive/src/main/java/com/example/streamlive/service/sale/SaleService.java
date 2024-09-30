package com.example.streamlive.service.sale;

import com.example.streamlive.dto.product.CheckOutDto;
import com.example.streamlive.model.order.Order;
import com.example.streamlive.model.order.OrderDetail;

import java.util.List;

public interface SaleService {
    Boolean checkout(CheckOutDto checkOutDto);
    List<Order> getUserOrders(Long userId);
    OrderDetail getOrderDetail(Long orderId);
}
