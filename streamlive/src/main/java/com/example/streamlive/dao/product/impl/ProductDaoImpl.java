package com.example.streamlive.dao.product.impl;

import com.example.streamlive.dao.product.ProductDao;
import com.example.streamlive.dto.product.ProductDto;
import com.example.streamlive.model.Product;
import com.example.streamlive.model.rowmapper.ClientProductRowMapper;
import com.example.streamlive.model.user.ClientProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
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
        String sql = "INSERT INTO delegation (product_id,client_id,agent_id,status) VALUES (:productId,:client,:agent,:status)";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("client", client);
        map.put("agent", null);
        map.put("status", 0);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<ClientProduct> findProductsForDelegation(Long userId, int status, String sortBy, String sortOrder){
        String sql = "SELECT \n" +
                "    u.id AS user_id,              \n" +
                "    u.name AS user_name,          \n" +
                "    u.image AS user_image,        \n" +
                "    p.id AS product_id,      \n" +
                "    p.name AS product_name,  \n" +
                "    p.description,           \n" +
                "    p.stock,                 \n" +
                "    p.price,                 \n" +
                "    p.main_image,            \n" +
                "    p.created_at            \n" +
                "FROM \n" +
                "    user u\n" +
                "JOIN \n" +
                "    product p ON u.id = p.user_id\n" +
                "JOIN \n" +
                "    delegation d ON p.id = d.product_id\n" +
                "WHERE \n" +
                "    u.id != :userId                \n" +
                "    AND d.status != :status       \n" +
                "ORDER BY " + sortBy + " " + sortOrder + "\n";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("status", status);

        SqlParameterSource paramSource = new MapSqlParameterSource(params);
        return namedParameterJdbcTemplate.query(sql, paramSource, new ClientProductRowMapper());
    }

    @Override
    public List<Product> findDelegatedProducts(int userId){
        String sql = "SELECT product.*, delegation.client_id, delegation.agent_id, delegation.status\n" +
                "FROM product\n" +
                "INNER JOIN delegation ON product.id = delegation.product_id\n" +
                "WHERE product.user_id = :userId " +
                "AND delegation.status = 1";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.query(sql, paramSource, new BeanPropertyRowMapper<>(Product.class));
    }

    @Override
    public List<Product> findUndelegatedProducts(int userId){
        String sql = "SELECT product.*, delegation.client_id, delegation.agent_id, delegation.status\n" +
                "FROM product\n" +
                "INNER JOIN delegation ON product.id = delegation.product_id\n" +
                "WHERE product.user_id = :userId " +
                "AND delegation.status = 0";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.query(sql, paramSource, new BeanPropertyRowMapper<>(Product.class));
    }

    @Override
    public List<Product> findMyDelegatedProducts(int userId){
        String sql = "SELECT product.*, delegation.client_id, delegation.agent_id, delegation.status\n" +
                "FROM product\n" +
                "INNER JOIN delegation ON product.id = delegation.product_id\n" +
                "WHERE delegation.agent_id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.query(sql, paramSource, new BeanPropertyRowMapper<>(Product.class));
    }

    @Override
    public Product findProductById(int productId){
        String sql = "SELECT * FROM product WHERE id = :productId";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(Product.class));
    }

    @Override
    public int findProductStockById(int productId) {
        String sql = "SELECT stock FROM product WHERE id = :productId";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("productId", productId);
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public Integer updateProductStockById(int productId, int stock){
        String sql = "UPDATE product SET stock = :stock WHERE id = :productId";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("stock", stock);
        paramMap.put("productId", productId);
        try {
            return namedParameterJdbcTemplate.update(sql, paramMap);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<ClientProduct> searchProductsByKeyword(Long userId, String keyword,int status,int limit, int offset){
        String sql = "SELECT \n" +
                "    u.id AS user_id,              \n" +
                "    u.name AS user_name,          \n" +
                "    u.image AS user_image,        \n" +
                "    p.id AS product_id,      \n" +
                "    p.name AS product_name,  \n" +
                "    p.description,           \n" +
                "    p.stock,                 \n" +
                "    p.price,                 \n" +
                "    p.main_image,            \n" +
                "    p.created_at             \n" +
                "FROM                         \n" +
                "    user u                   \n" +
                "JOIN                         \n" +
                "    product p ON u.id = p.user_id\n" +
                "JOIN                         \n" +
                "    delegation d ON p.id = d.product_id\n" +
                "WHERE                              \n" +
                "    u.id != :userId                \n" +
                "    AND d.status != :status        \n" +
                "    AND p.name LIKE CONCAT('%', :keyword, '%')    \n" +
                "LIMIT :limit                       \n" +
                "OFFSET :offset";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("status", status);
        params.put("keyword", keyword);
        params.put("limit", limit);
        params.put("offset", offset);
        SqlParameterSource paramSource = new MapSqlParameterSource(params);

        return namedParameterJdbcTemplate.query(sql, paramSource, new ClientProductRowMapper());
    }
}
