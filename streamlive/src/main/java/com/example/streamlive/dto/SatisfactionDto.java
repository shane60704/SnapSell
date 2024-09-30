package com.example.streamlive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SatisfactionDto {
    private Long userId;
    private String liveId;
    private int score;
    private String comment;
}
