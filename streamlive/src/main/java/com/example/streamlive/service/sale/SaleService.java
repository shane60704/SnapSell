package com.example.streamlive.service.sale;

import com.example.streamlive.dto.product.CheckOutDto;

public interface SaleService {
    Boolean checkout(CheckOutDto checkOutDto);
}
