package com.example.streamlive.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    @JsonProperty("client_id")
    private int id;
    @JsonProperty("client_name")
    private String name;
}
