package com.cs.coding.test.service.impl;

import com.cs.coding.test.repository.LogEventRepository;
import com.cs.coding.test.service.FileParser;
import com.cs.coding.test.entity.LogEventEntity;
import com.cs.coding.test.model.LogEventModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
public class LogFileParser implements FileParser {

    private LogEventRepository logEventRepository;

    @Autowired
    LogFileParser(LogEventRepository logEventRepository){
        this.logEventRepository = logEventRepository;
    }

    @Override
    public void parseFile(String path) {
        Map<String, LogEventEntity> logEventMap = new ConcurrentHashMap<>();
        try(Stream<String> lines = Files.lines(Paths.get(path.trim()))) {
            lines.forEach(line -> {
                ExecutorService threadPool = Executors.newFixedThreadPool(1000);
                threadPool.submit(()-> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        LogEventModel logEventModel = mapper.readValue(line.trim(),LogEventModel.class);
                        LogEventEntity logEventEntity = new LogEventEntity();
                        if(!CollectionUtils.isEmpty(logEventMap)){
                            logEventEntity = logEventMap.get(logEventModel.getId());
                            if(!ObjectUtils.isEmpty(logEventEntity)){
                                Long oldTimeStamp = logEventEntity.getEventDuration();
                                Long duration = oldTimeStamp > logEventModel.getTimestamp() ?
                                        oldTimeStamp - logEventModel.getTimestamp() : logEventModel.getTimestamp() - oldTimeStamp;
                                Boolean alert = duration.intValue() > 4 ? true : false;
                                //Check whether particular log event already there in database
                                this.logEventRepository.findLogEventByEventId(logEventEntity.getEventId()).map(eventEntity -> {
                                    eventEntity.setEventType(logEventModel.getType());
                                    eventEntity.setEventDuration(duration);
                                    eventEntity.setEventAlert(alert);
                                    eventEntity.setEventHost(logEventModel.getHost());
                                    return logEventRepository.save(eventEntity);
                                }).orElseGet(()->{
                                    LogEventEntity eventEntity =  new LogEventEntity();
                                    eventEntity.setEventId(logEventModel.getId());
                                    eventEntity.setEventType(logEventModel.getType());
                                    eventEntity.setEventDuration(duration);
                                    eventEntity.setEventAlert(alert);
                                    eventEntity.setEventHost(logEventModel.getHost());
                                    return logEventRepository.save(eventEntity);
                                });
                            } else {
                                logEventEntity = new LogEventEntity();
                                logEventEntity.setEventId(logEventModel.getId());
                                logEventEntity.setEventType(logEventModel.getType());
                                logEventEntity.setEventHost(logEventModel.getHost());
                                logEventEntity.setEventDuration(logEventModel.getTimestamp());
                                logEventEntity.setEventAlert(false);
                                logEventMap.put(logEventModel.getId(),logEventEntity);
                            }
                        } else {
                            logEventEntity.setEventId(logEventModel.getId());
                            logEventEntity.setEventType(logEventModel.getType());
                            logEventEntity.setEventHost(logEventModel.getHost());
                            logEventEntity.setEventDuration(logEventModel.getTimestamp());
                            logEventMap.put(logEventModel.getId(),logEventEntity);
                        }
                    } catch (JsonProcessingException jsonProcessingException) {
                        jsonProcessingException.printStackTrace();
                    }
                });
            });
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
