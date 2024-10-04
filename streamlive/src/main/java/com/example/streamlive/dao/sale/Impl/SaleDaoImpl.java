package com.example.streamlive.dao.sale.Impl;

import com.example.streamlive.dao.sale.SaleDao;
import com.example.streamlive.dto.product.CheckOutDto;
import com.example.streamlive.model.order.Order;
import com.example.streamlive.model.order.OrderDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SaleDaoImpl implements SaleDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int createOrder(CheckOutDto checkOutDto) {
        String sql = "INSERT INTO `order` (user_id,product_id,live_id,quantity,total_price,freight,order_time,status) VALUES" +
                "(:userId,:productId,:liveId,:quantity,:totalPrice,:freight,:orderTime,:status)";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", checkOutDto.getUserId());
        map.put("productId", checkOutDto.getProductId());
        map.put("liveId", checkOutDto.getLiveId());
        map.put("quantity", checkOutDto.getQuantity());
        map.put("totalPrice", checkOutDto.getTotalPrice());
        map.put("freight", checkOutDto.getFreight());
        map.put("orderTime", checkOutDto.getOrderTime());
        map.put("status", 0);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public int createRecipent(CheckOutDto checkOutDto, int orderId) {
        String sql = "INSERT INTO `recipent` (name, phone, email, address, order_id) VALUES" +
                "(:name, :phone, :email, :address, :order_id)";

        Map<String, Object> map = new HashMap<>();
        map.put("name", checkOutDto.getRecipentDto().getName());
        map.put("phone", checkOutDto.getRecipentDto().getPhone());
        map.put("email", checkOutDto.getRecipentDto().getEmail());
        map.put("address", checkOutDto.getRecipentDto().getAddress());
        map.put("order_id", orderId);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public int updateOrderStatus(int orderId, int status) {
        String sql = "UPDATE `order` SET status=:status WHERE id=:orderId";
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("orderId", orderId);
        return namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        String sql = "SELECT id, order_time AS orderTime, total_price AS totalPrice, logistics_status AS logisticsStatus " +
                "FROM `order` " +
                "WHERE user_id=:userId " +
                "ORDER BY id DESC";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        return namedParameterJdbcTemplate.query(sql, map, new BeanPropertyRowMapper<>(Order.class));
    }

    @Override
    public OrderDetail getOrderDetailByOrderId(Long orderId) {
        String sql = "SELECT " +
                "o.id AS orderId, " +
                "u.name AS userName, " +
                "p.name AS productName, " +
                "p.price AS productPrice, " +
                "o.quantity, " +
                "o.total_price AS totalPrice, " +
                "o.order_time AS orderTime, " +
                "r.name AS recipientName, " +
                "r.phone AS recipientPhone, " +
                "r.email AS recipientEmail, " +
                "r.address AS recipientAddress " +
                "FROM `order` o " +
                "JOIN `user` u ON o.user_id = u.id " +
                "JOIN product p ON o.product_id = p.id " +
                "JOIN recipent r ON o.id = r.order_id " +
                "WHERE o.id = :orderId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orderId", orderId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(OrderDetail.class));
    }


}
