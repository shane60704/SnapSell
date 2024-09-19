package com.example.streamlive.controller;

import com.example.streamlive.dto.ErrorResponseDto;
import com.example.streamlive.dto.product.CheckOutDto;
import com.example.streamlive.service.sale.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
