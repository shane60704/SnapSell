package com.example.streamlive.model.livestream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiveSummary {
    private Long liveCount;
    private double averageScore;
    private Long commentCount;
}
