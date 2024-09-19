package com.example.streamlive.dao.product.impl;

import com.example.streamlive.dao.product.ProductDao;
import com.example.streamlive.dto.product.ProductDto;
import com.example.streamlive.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ProductDaoImpl implements ProductDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer createProduct(ProductDto productDto, String mainImagePath,String feature){
        String sql = "INSERT INTO product (user_id,name,feature,description,stock,price,main_image,commission) VALUES " +
                "(:user_id,:name,:feature,:description,:stock,:price,:main_image,:commission)";
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", productDto.getUserId());
        map.put("name", productDto.getName());
        map.put("feature", feature);
        map.put("description", productDto.getDescription());
        map.put("stock", productDto.getStock());
        map.put("price", productDto.getPrice());
        map.put("main_image", mainImagePath);
        map.put("commission",0);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new MapSqlParameterSource(map);

        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }
    @Override
    public Integer createDelegation(Integer productId,String client){
        String sql = "INSERT INTO delegation (product_id,client,agent,status) VALUES (:productId,:client,:agent,:status)";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("client", client);
        map.put("agent", "none");
        map.put("status", 0);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<Product> findProductsForDelegation(int userId){
        String sql = "SELECT product.*, delegation.client, delegation.agent, delegation.status\n" +
                "FROM product\n" +
                "INNER JOIN delegation ON product.id = delegation.product_id\n" +
                "WHERE product.user_id != :userId \n" +
                "AND delegation.status != 1";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.query(sql, paramSource, new BeanPropertyRowMapper<>(Product.class));
    }
}
