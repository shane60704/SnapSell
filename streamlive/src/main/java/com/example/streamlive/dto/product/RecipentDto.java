package com.example.streamlive.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipentDto {
    private String name;
    private String phone;
    private String email;
    private String address;
}
