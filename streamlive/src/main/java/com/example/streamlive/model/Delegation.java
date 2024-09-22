package com.example.streamlive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Delegation {
    private int id;
    private int productId;
    private int clientId;
    private int agentId;
    private int status;
}
