package com.cs.coding.test.service.impl;

import com.cs.coding.test.repository.LogEventRepository;
import com.cs.coding.test.service.FileParser;
import com.cs.coding.test.entity.LogEvent;
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
        Map<String,LogEvent> logEventMap = new ConcurrentHashMap<>();
        try(Stream<String> lines = Files.lines(Paths.get(path.trim()))) {
            lines.forEach(line -> {
                ExecutorService threadPool = Executors.newFixedThreadPool(100);
                threadPool.submit(()-> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        LogEventModel logEventModel = mapper.readValue(line.trim(),LogEventModel.class);
                        LogEvent logEvent = new LogEvent();
                        if(!CollectionUtils.isEmpty(logEventMap)){
                            logEvent = logEventMap.get(logEventModel.getId());
                            if(!ObjectUtils.isEmpty(logEvent)){
                                Long oldTimeStamp = logEvent.getEventDuration();
                                Long duration = oldTimeStamp > logEventModel.getTimestamp() ? oldTimeStamp - logEventModel.getTimestamp() : logEventModel.getTimestamp() - oldTimeStamp;
                                logEvent.setEventDuration(duration);
                                logEvent.setEventAlert(duration.intValue() > 4 ? true : false);
                                this.logEventRepository.save(logEvent);
                            } else {
                                logEvent = new LogEvent();
                                logEvent.setEventId(logEventModel.getId());
                                logEvent.setEventType(logEventModel.getType());
                                logEvent.setEventHost(logEventModel.getHost());
                                logEvent.setEventDuration(logEventModel.getTimestamp());
                                logEvent.setEventAlert(false);
                                logEventMap.put(logEventModel.getId(),logEvent);
                            }
                        } else {
                            logEvent.setEventId(logEventModel.getId());
                            logEvent.setEventType(logEventModel.getType());
                            logEvent.setEventHost(logEventModel.getHost());
                            logEvent.setEventDuration(logEventModel.getTimestamp());
                            logEventMap.put(logEventModel.getId(),logEvent);
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

    public void findAllContent(){
        List<LogEvent> logEvents = this.logEventRepository.findAll();
        System.out.println(logEvents.size());
    }
}
