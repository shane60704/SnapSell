package com.example.streamlive.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfile {
    private Long id;
    private String name;
    private String image;
    private String backgroundImage;
    private String description;
    private Long followers;
    private Long totalProducts;
    private Long averageScore;
}
