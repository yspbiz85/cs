package com.cs.coding.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogEventModel {
    private String id;
    private String state;
    private String type;
    private String host;
    private Long timestamp;
}
