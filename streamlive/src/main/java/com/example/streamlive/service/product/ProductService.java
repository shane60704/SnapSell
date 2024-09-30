package com.example.streamlive.service.product;

import com.example.streamlive.dto.product.ProductDto;
import com.example.streamlive.model.Product;
import com.example.streamlive.model.user.ClientProduct;

import java.util.List;
import java.util.Map;

public interface ProductService {
    boolean listProduct(ProductDto productDto);
    List<ClientProduct> getProductsForDelegation(Long userId, int status,String sortBy,String sortOrder);
    List<Product> getDelegatedProducts(int userId);
    List<Product> getUndelegatedProducts(int userId);
    List<Product> getMyDelegatedProducts(int userId);
    Product getProductInfo(int productId);
    Map<String, Object> searchProduct(Long userId, String keyword, int status, int paging);
}
