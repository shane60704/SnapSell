package com.example.streamlive.model.rowmapper;

import com.example.streamlive.model.product.ProductInfo;
import com.example.streamlive.model.user.ClientInfo;
import com.example.streamlive.model.user.ClientProduct;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientProductRowMapper implements RowMapper<ClientProduct> {

    @Override
    public ClientProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
        // 封裝 ClientInfo
        ClientInfo clientInfo = new ClientInfo(
                rs.getLong("user_id"),       // user 表的 id
                rs.getString("user_name"),  // user 表的 name
                rs.getString("user_image")  // user 表的 image
        );

        // 封裝 ProductInfo
        ProductInfo productInfo = new ProductInfo(
                rs.getLong("product_id"),        // product 表的 id
                rs.getString("product_name"),   // product 表的 name
                rs.getString("description"),    // product 表的 description
                rs.getLong("stock"),             // product 表的 stock
                rs.getLong("price"),
                rs.getString("main_image"),      // product 表的 main_image
                rs.getString("created_at")
        );

        // 封裝 ClientProduct
        return new ClientProduct(clientInfo, productInfo);
    }
}
