package com.example.models;

import lombok.Data;

@Data
public class ProductResponse {
    private int id;
    private String title;
    private double price;
    private String description;
    private String category;
    private Product.Metadata metadata;
}