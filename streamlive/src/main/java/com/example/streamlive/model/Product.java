package com.example.streamlive.model;

import lombok.Data;

@Data
public class Product {
    private int id;
    private int userId;
    private String name;
    private String feature;
    private String description;
    private int stock;
    private int price;
    private String mainImage;
    private int commission;
}
