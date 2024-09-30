package com.example.streamlive.controller;

import com.example.streamlive.dto.ErrorResponseDto;
import com.example.streamlive.dto.product.ProductDto;
import com.example.streamlive.dto.response.ApiResponse;
import com.example.streamlive.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/product")
public class ProductController {
    private final ProductService productService;
    private final ContractController contractController;

    // 上架商品 API
    @PostMapping("/listProduct")
    public ResponseEntity<?> listProduct(@ModelAttribute ProductDto productDto) {
        log.info("productDto: {}", productDto.toString());
        if (productService.listProduct(productDto)) {
            return ResponseEntity.ok("Success");
        } else {
            return new ResponseEntity<>(ErrorResponseDto.error("failed"), HttpStatus.BAD_REQUEST);
        }
    }

    // 可代理商品清單 API status = 0 (可代理)、 status = 1 (不可代理)
    @GetMapping("/for-delegation")
    public ResponseEntity<?> getProductsForDelegation(
            @RequestParam("userId") Long userId,
            @RequestParam("status") int status,
            @RequestParam(value = "sortBy", defaultValue = "created_at") String sortBy, // 默認排序欄位為 created_at (上架時間)
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder // 默認排序順序為desc (降序)
    ) {
        log.info("userId"+userId);
        log.info("status"+status);
        log.info("sortBy"+sortBy);
        log.info("sortOrder"+sortOrder);
        return ResponseEntity.ok(new ApiResponse<>(productService.getProductsForDelegation(userId, status, sortBy, sortOrder)));
    }

    // 已上架且未被代理的商品 API
    @GetMapping("/undelgated")
    public ResponseEntity<?> getAvailableAndUndelegatedProducts(@RequestParam("userId") int userId) {
        return ResponseEntity.ok(new ApiResponse<>(productService.getUndelegatedProducts(userId)));
    }

    // 已上架且被代理的商品 API
    @GetMapping("/delegated")
    public ResponseEntity<?> getDelegatedProducts(@RequestParam("userId") int userId) {
        return ResponseEntity.ok(new ApiResponse<>(productService.getDelegatedProducts(userId)));
    }

    // 已代理商品清單 API
    @GetMapping("/my-delegations")
    public ResponseEntity<?> getMyDelegatedProducts(@RequestParam("userId") int userId) {
        return ResponseEntity.ok(new ApiResponse<>(productService.getMyDelegatedProducts(userId)));
    }

    // 取得商品資訊 API
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductInfo(@PathVariable("productId") int productId) {
        return ResponseEntity.ok(new ApiResponse<>(productService.getProductInfo(productId)));
    }

    // 搜尋商品資訊 API
    @GetMapping("/search")
    public ResponseEntity<?> searchProduct(@RequestParam("userId") Long userId,
                                           @RequestParam("keyword") String keyword,
                                           @RequestParam("status") int status,
                                           @RequestParam(value = "paging" , defaultValue = "0") int paging)
    {
        return ResponseEntity.ok(new ApiResponse<>(productService.searchProduct(userId,keyword,status,paging)));
    }
}
