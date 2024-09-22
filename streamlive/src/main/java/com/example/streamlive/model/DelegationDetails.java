package com.example.streamlive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelegationDetails {
    private int id;
    private int delegationId;
    private String client;
    private String agent;
    private String product;
    private String salesPeriod;
    private String commissionRate;
    private String clientSignature;
    private String agentSignature;
    private String createdAt;
}
