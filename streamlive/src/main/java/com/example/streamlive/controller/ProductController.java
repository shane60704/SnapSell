package com.example.streamlive.controller;

import com.example.streamlive.dto.ErrorResponseDto;
import com.example.streamlive.dto.product.ProductDto;
import com.example.streamlive.dto.response.ApiResponse;
import com.example.streamlive.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/product")
public class ProductController {
    private final ProductService productService;

    // 上架商品 API
    @PostMapping("/listProduct")
    public ResponseEntity<?> listProduct(@ModelAttribute ProductDto productDto) {
        if (productService.listProduct(productDto)) {
            return ResponseEntity.ok("Success");
        }else{
            return new ResponseEntity<>(ErrorResponseDto.error("failed"), HttpStatus.BAD_REQUEST);
        }
    }

    // 可代理商品清單 API
    @GetMapping("/for-delegation")
    public ResponseEntity<?> getProductsForDelegation(@RequestParam("userId") int userId) {
        return ResponseEntity.ok(new ApiResponse<>(productService.getProductsForDelegation(userId)));
    }

//    // 已委託商品 API
//    @GetMapping("/delegated")
//    public ResponseEntity<?> getDelegatedProducts(){}
//
//    // 已上架商品 API
//    @GetMapping("/available")
//    public ResponseEntity<?> getAvailableProducts(){}
//
//    // 已代理商品清單 API
//    @GetMapping("/my-delegations")
//    public ResponseEntity<?> getMyDelegatedProducts(){}
//
//    // 取得商品資訊API
//    @GetMapping("/{productId}")
//    public ResponseEntity<?> getProductInfo(@PathVariable("productId") int productId) {
//        // 這裡的 productId 變數已經從 URL 中提取出來，可以直接使用
//        // 實現查詢商品資訊的邏輯
//        return ResponseEntity.ok("Product ID: " + productId); // 示例回應
//    }

}
