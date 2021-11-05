package com.cs.coding.test.service.impl;

import com.cs.coding.test.entity.LogEventEntity;
import com.cs.coding.test.model.LogEventModel;
import com.cs.coding.test.repository.LogEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileParserThread implements Runnable{
    private String line;
    Map<String, LogEventEntity> logEventEntityMap;
    @Autowired
    private LogEventRepository logEventRepository;

    FileParserThread(String line, Map<String, LogEventEntity> logEventEntityMap){
        this.line = line;
        this.logEventEntityMap = logEventEntityMap;
    }
    @Override
    public void run() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            LogEventModel logEventModel = mapper.readValue(line.trim(), LogEventModel.class);
            if (logEventEntityMap.get(logEventModel.getId()) != null) {
                LogEventEntity logEventEntity = logEventEntityMap.get(logEventModel.getId());
                Long oldTimeStamp = logEventEntity.getEventDuration();
                Long duration = oldTimeStamp > logEventModel.getTimestamp() ?
                        oldTimeStamp - logEventModel.getTimestamp() : logEventModel.getTimestamp() - oldTimeStamp;
                Boolean alert = duration.intValue() > 4;
                logEventEntity.setEventDuration(duration);
                logEventEntity.setEventAlert(alert);
                this.logEventRepository.save(logEventEntity);
                //logEventEntityMap.put(logEventModel.getId(), logEventEntity);
            } else {
                LogEventEntity logEventEntity = new LogEventEntity();
                logEventEntity.setEventId(logEventModel.getId());
                logEventEntity.setEventType(logEventModel.getType());
                logEventEntity.setEventDuration(logEventModel.getTimestamp());
                logEventEntity.setEventAlert(false);
                logEventEntity.setEventHost(logEventModel.getHost());
                logEventEntityMap.put(logEventModel.getId(), logEventEntity);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Map<String, LogEventEntity> getLogEventEntityMap() {
        return logEventEntityMap;
    }

    public void setLogEventEntityMap(Map<String, LogEventEntity> logEventEntityMap) {
        this.logEventEntityMap = logEventEntityMap;
    }
}
