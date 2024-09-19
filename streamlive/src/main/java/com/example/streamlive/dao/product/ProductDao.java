package com.example.streamlive.dao.product;

import com.example.streamlive.dto.product.ProductDto;
import com.example.streamlive.model.Product;

import java.util.List;

public interface ProductDao {
    Integer createProduct(ProductDto productDto, String mainImagePath,String feature);
    Integer createDelegation(Integer productId,String client);
    List<Product> findProductsForDelegation(int userId);
    List<Product> findDelegatedProducts(int userId);
    List<Product> findUndelegatedProducts(int userId);
    List<Product> findMyDelegatedProducts(int userId);
    Product findProductById(int productId);
}
