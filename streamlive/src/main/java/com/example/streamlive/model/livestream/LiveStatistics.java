package com.example.streamlive.model.livestream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiveStatistics {
    private Long totalViewers;
    private Long totalQuantity;
    private Double totalFigures;
    private Long totalDelegationCount;
    private Double averageScore;
}
