package com.example.streamlive.model.user;

import com.example.streamlive.model.product.ProductInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProduct {
    private ClientInfo clientInfo;
    private ProductInfo productInfo;
}
