package com.example.streamlive.model.livestream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Satisfaction {
    private Long satisfactionId;
    private int score;
    private String comment;
    private String name;
    private String image;
}
