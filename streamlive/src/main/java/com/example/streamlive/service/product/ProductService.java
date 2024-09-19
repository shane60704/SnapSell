package com.example.streamlive.service.product;

import com.example.streamlive.dto.product.ProductDto;
import com.example.streamlive.model.Product;

import java.util.List;

public interface ProductService {
    boolean listProduct(ProductDto productDto);
    List<Product> getProductsForDelegation(int userId);
    List<Product> getDelegatedProducts(int userId);
    List<Product> getUndelegatedProducts(int userId);
}
