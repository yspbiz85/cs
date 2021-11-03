package com.cs.coding.test.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "LogEvent")
public class LogEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String eventId;
    private Long eventDuration;
    private String eventType;
    private String eventHost;
    private Boolean eventAlert;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(Long eventDuration) {
        this.eventDuration = eventDuration;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventHost() {
        return eventHost;
    }

    public void setEventHost(String eventHost) {
        this.eventHost = eventHost;
    }

    public Boolean getEventAlert() {
        return eventAlert;
    }

    public void setEventAlert(Boolean eventAlert) {
        this.eventAlert = eventAlert;
    }

    @Override
    public String toString() {
        return "LogEvent{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", eventDuration=" + eventDuration +
                ", eventType='" + eventType + '\'' +
                ", eventHost='" + eventHost + '\'' +
                ", eventAlert=" + eventAlert +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEventEntity logEvent = (LogEventEntity) o;
        return id == logEvent.id && Objects.equals(eventId, logEvent.eventId) && Objects.equals(eventDuration, logEvent.eventDuration) && Objects.equals(eventType, logEvent.eventType) && Objects.equals(eventHost, logEvent.eventHost) && Objects.equals(eventAlert, logEvent.eventAlert);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, eventDuration, eventType, eventHost, eventAlert);
    }
}
