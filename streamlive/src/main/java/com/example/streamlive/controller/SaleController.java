package com.example.streamlive.controller;

import com.example.streamlive.dto.ErrorResponseDto;
import com.example.streamlive.dto.product.CheckOutDto;
import com.example.streamlive.dto.response.ApiResponse;
import com.example.streamlive.service.sale.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/1.0/order")
public class SaleController {

    private final SaleService saleService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckOutDto checkOutDto) {
        if (saleService.checkout(checkOutDto)) {
            return ResponseEntity.ok("success");
        }
        return new ResponseEntity<>(ErrorResponseDto.error("failed"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(new ApiResponse<>(saleService.getUserOrders(userId)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(new ApiResponse<>(saleService.getOrderDetail(orderId)));
    }
}
