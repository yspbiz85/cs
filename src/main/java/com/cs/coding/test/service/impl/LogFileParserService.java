package com.cs.coding.test.service.impl;

import com.cs.coding.test.repository.LogEventRepository;
import com.cs.coding.test.service.IFileParserService;
import com.cs.coding.test.entity.LogEventEntity;
import com.cs.coding.test.model.LogEventModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class LogFileParserService implements IFileParserService {

    private static final Logger logger = LoggerFactory.getLogger(LogFileParserService.class);

    @Value("${fixed.thread.pool.size}")
    private Integer FIXED_THREAD_POOL_SIZE;

    @Value("${alert.flag.max.time}")
    private Integer ALERT_FLAG_MAX_TIME;

    private final LogEventRepository logEventRepository;

    @Autowired
    LogFileParserService(LogEventRepository logEventRepository){
        this.logEventRepository = logEventRepository;
    }

    @Override
    public void parseFile(String path) {
        Map<String, LogEventEntity> logEventMap = new ConcurrentHashMap<>();
        final long startTime = System.nanoTime();
        long fileSize = 0;
        logger.debug("Creating the thread pool of the size : {}",FIXED_THREAD_POOL_SIZE);
        ExecutorService threadPool = Executors.newFixedThreadPool(FIXED_THREAD_POOL_SIZE);

        //Read the content of the file line by line in single thread order reduce the memory consumption
        logger.debug("Reading the content of the file : {}",path);
        try(Stream<String> eventLines = Files.lines(Paths.get(path.trim()))) {
            FileChannel fileChannel = FileChannel.open(Paths.get(path.trim()));
            fileSize = fileChannel.size();
            fileChannel.close();
            eventLines.forEach(eventLine -> {
                logger.debug("Processing the event line : {}",eventLine);
                //Delegate the event line to one of the thread pool created above,in order to process the same
                threadPool.submit(()-> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        LogEventModel logEventModel = mapper.readValue(eventLine.trim(),LogEventModel.class);
                        LogEventEntity logEventEntity = new LogEventEntity();
                        if(!CollectionUtils.isEmpty(logEventMap)){
                            //Check whether there is previous entry present in map for given event id
                            logEventEntity = logEventMap.get(logEventModel.getId());
                            if(!ObjectUtils.isEmpty(logEventEntity)){
                                Long oldTimeStamp = logEventEntity.getEventDuration();
                                Long duration = oldTimeStamp > logEventModel.getTimestamp() ?
                                        oldTimeStamp - logEventModel.getTimestamp() : logEventModel.getTimestamp() - oldTimeStamp;
                                Boolean alert = duration.intValue() > ALERT_FLAG_MAX_TIME;
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
                        logger.error("JsonProcessingException : {}",jsonProcessingException.getMessage());
                    }
                });
            });
        } catch (IOException ioException) {
            logger.error("IOException : {}",ioException.getMessage());
        }
        threadPool.shutdown();
        try {
            boolean isThreadPoolTerminated = threadPool.awaitTermination(10, TimeUnit.SECONDS);
            logger.debug("isThreadPoolTerminated : {}",isThreadPoolTerminated);
        } catch (InterruptedException interruptedException) {
            logger.error("InterruptedException : {}",interruptedException.getMessage());
        }
        logger.info("File Path : {} ",path);
        logger.info("File size : {} ",fileSize);
        logger.info("Total time required to parse : {}",System.nanoTime() - startTime);
    }

    public void finaAllEventData(){
        List<LogEventEntity> eventEntities = this.logEventRepository.findAll();
        logger.debug("Total 1 :: {}",eventEntities.size());
        printLogEventData(eventEntities);
    }

    public void finaEventById(String eventId){
        this.logEventRepository.findLogEventByEventId(eventId).ifPresent(this::printLogEventData);
    }

    public void finaEventByAlert(Boolean eventAlert){
        List<LogEventEntity> eventEntities = this.logEventRepository.findLogEventByEventAlert(eventAlert);
        printLogEventData(eventEntities);
    }

    private void printLogEventData(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonData = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            final String filename = UUID.randomUUID().toString().replace("-", "")+".txt";
            logger.info("Output data written to file : {}",filename);
            Files.write(Paths.get(filename),jsonData.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException jsonProcessingException) {
            logger.error("JsonProcessingException : {}",jsonProcessingException.getMessage());
        } catch (IOException ioException) {
            logger.error("IOException : {}",ioException.getMessage());
        } catch (Exception exception) {
            logger.error("Internal Error : {}",exception.getMessage());
        }
    }
}
