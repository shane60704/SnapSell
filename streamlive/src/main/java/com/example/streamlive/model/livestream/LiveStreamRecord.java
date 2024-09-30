package com.example.streamlive.model.livestream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiveStreamRecord {
    private int id;
    private int userId;
    private Integer viewers;
    private Integer salesQuantity;
    private Integer salesFigures;
    private String liveId;
    private Timestamp startTime;
    private Timestamp endTime;
}
