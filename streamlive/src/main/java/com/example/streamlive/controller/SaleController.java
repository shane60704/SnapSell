package com.example.streamlive.controller;

import com.example.streamlive.dto.ErrorResponseDto;
import com.example.streamlive.dto.product.CheckOutDto;
import com.example.streamlive.dto.response.APIResponse;
import com.example.streamlive.exception.custom.*;
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
        saleService.checkout(checkOutDto);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(new APIResponse<>(saleService.getUserOrders(userId)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(new APIResponse<>(saleService.getOrderDetail(orderId)));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInsufficientStockException(InsufficientStockException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StockUpdateFailedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleStockUpdateFailedException(StockUpdateFailedException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderCreationFailedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleOrderCreationFailedException(OrderCreationFailedException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecipientCreationFailedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleRecipientCreationFailedException(RecipientCreationFailedException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorResponseDto<String>> handlePaymentProcessingException(PaymentProcessingException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
