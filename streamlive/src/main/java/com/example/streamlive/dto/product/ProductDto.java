package com.example.streamlive.dto.product;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductDto {
    private String userId;
    private String name;
    private String description;
    private List<String> feature;
    private int price;
    private int stock;
    private MultipartFile mainImage;
}
