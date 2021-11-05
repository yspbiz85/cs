package com.cs.coding.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogEventHelper {
    private String eventId;
    private Long eventStart;
    private Long eventEnd;
    private Long eventDuration;
    private String eventType;
    private String eventHost;
    private Boolean eventAlert;
}
