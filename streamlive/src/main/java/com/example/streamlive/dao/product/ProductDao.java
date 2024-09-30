package com.example.streamlive.dao.product;

import com.example.streamlive.dto.product.ProductDto;
import com.example.streamlive.model.Product;
import com.example.streamlive.model.user.ClientProduct;

import java.util.List;

public interface ProductDao {
    Integer createProduct(ProductDto productDto, String mainImagePath,String feature);
    Integer createDelegation(Integer productId,String client);
    List<ClientProduct> findProductsForDelegation(Long userId, int status, String sortBy, String sortOrder);
    List<Product> findDelegatedProducts(int userId);
    List<Product> findUndelegatedProducts(int userId);
    List<Product> findMyDelegatedProducts(int userId);
    Product findProductById(int productId);
    int findProductStockById(int productId);
    Integer updateProductStockById(int productId, int stock);
    List<ClientProduct> searchProductsByKeyword(Long userId, String keyword,int status,int limit, int offset);
}
