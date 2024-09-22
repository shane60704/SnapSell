package com.example.streamlive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelegationList {
    private int delegationId;
    private Product product;
    private Client client;
    private Agent agent;
}
