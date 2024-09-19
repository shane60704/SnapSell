package com.example.streamlive.dao.sale.Impl;

import com.example.streamlive.dao.sale.SaleDao;
import com.example.streamlive.dto.product.CheckOutDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SaleDaoImpl implements SaleDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int createOrder (CheckOutDto checkOutDto) {
        String sql = "INSERT INTO `order` (user_id,product_id,live_id,quantity,total_price,freight,order_time,status) VALUES" +
                "(:userId,:productId,:liveId,:quantity,:totalPrice,:freight,:orderTime,:status)";
        Map<String, Object> map = new HashMap<>();
        map.put("userId",checkOutDto.getUserId());
        map.put("productId",checkOutDto.getProductId());
        map.put("liveId",checkOutDto.getLiveId());
        map.put("quantity",checkOutDto.getQuantity());
        map.put("totalPrice",checkOutDto.getTotalPrice());
        map.put("freight",checkOutDto.getFreight());
        map.put("orderTime",checkOutDto.getOrderTime());
        map.put("status",0);
        // 使用 KeyHolder 來保存自動生成的 ID
        KeyHolder keyHolder = new GeneratedKeyHolder();
        // 執行插入操作
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder, new String[]{"id"});
        // 返回生成的訂單 ID
        return keyHolder.getKey().intValue();
    }

    @Override
    public int createRecipent(CheckOutDto checkOutDto, int orderId) {
        String sql = "INSERT INTO `recipent` (name, phone, email, address, order_id) VALUES" +
                "(:name, :phone, :email, :address, :order_id)";

        // 創建參數映射表
        Map<String, Object> map = new HashMap<>();
        map.put("name", checkOutDto.getRecipentDto().getName());
        map.put("phone", checkOutDto.getRecipentDto().getPhone());
        map.put("email", checkOutDto.getRecipentDto().getEmail());
        map.put("address", checkOutDto.getRecipentDto().getAddress());
        map.put("order_id", orderId);  // 來自方法參數的訂單ID

        // 使用 KeyHolder 來捕獲自動生成的鍵值（如有需要）
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // 執行插入操作
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder, new String[]{"id"});

        // 返回自動生成的 recipient ID（假設表有自動生成的主鍵）
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

}
