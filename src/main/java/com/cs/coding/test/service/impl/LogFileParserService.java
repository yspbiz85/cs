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
import org.springframework.util.ObjectUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;
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
    LogFileParserService(LogEventRepository logEventRepository) {
        this.logEventRepository = logEventRepository;
    }


    public void parseFile(String path) {
        ExecutorService producerExecutor = Executors.newFixedThreadPool(2);
        ExecutorService consumerExecutor = Executors.newFixedThreadPool(2);

        ConcurrentHashMap<String, LogEventEntity> logEventEntityMap = new ConcurrentHashMap<>();
        LinkedBlockingQueue<LogEventEntity> logEventEntityQueue = new LinkedBlockingQueue<>();

       CompletableFuture.runAsync(() -> {
        try (Stream<String> logEventLines = Files.lines(Paths.get(path.trim()))) {
            logEventLines.forEach(logEventLine -> CompletableFuture.runAsync(() -> {
                //Asynchronously handles the job of parsing the event data.
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    LogEventModel logEventModel = mapper.readValue(logEventLine.trim(), LogEventModel.class);
                    String eventId = logEventModel.getId();
                    LogEventEntity logEventEntity = logEventEntityMap.get(eventId);
                    if(ObjectUtils.isEmpty(logEventEntity)){
                        logEventEntity = new LogEventEntity();
                        logEventEntity.setEventId(logEventModel.getId());
                        logEventEntity.setEventType(logEventModel.getType());
                        logEventEntity.setEventHost(logEventModel.getHost());
                        logEventEntity.setEventDuration(logEventModel.getTimestamp());
                        logEventEntity.setEventAlert(false);
                        logEventEntityMap.put(logEventModel.getId(),logEventEntity);
                    } else {
                        Long oldTimeStamp = logEventEntity.getEventDuration();
                        Long duration = oldTimeStamp > logEventModel.getTimestamp() ?
                                oldTimeStamp - logEventModel.getTimestamp() : logEventModel.getTimestamp() - oldTimeStamp;
                        Boolean alert = duration.intValue() > ALERT_FLAG_MAX_TIME;
                        logEventEntity.setEventDuration(duration);
                        logEventEntity.setEventAlert(alert);
                        logEventEntityQueue.offer(logEventEntity);
                    }
                } catch (JsonProcessingException jsonProcessingException) {
                    logger.error("JsonProcessingException : {}", jsonProcessingException.getMessage());
                }
            }, producerExecutor));
        } catch (IOException ioException) {
            logger.error("IOException : {}", ioException.getMessage());
        }}, producerExecutor);

        CompletableFuture.runAsync(() -> {
            //Asynchronously handles the job of inserting data into DB.
            try {
                while (true) {
                    LogEventEntity logEventEntity = logEventEntityQueue.take();
                    //System.out.println("Thread "+Thread.currentThread().getName()+"processing the log event having id "+logEventEntity.getEventId());
                    this.logEventRepository.save(logEventEntity);
                    Thread.sleep(200);
                }
            } catch (InterruptedException interruptedException) {
                logger.error("InterruptedException : {}", interruptedException.getMessage());
            }
        }, consumerExecutor);
    }

    public void finaAllEventData() {
        List<LogEventEntity> eventEntities = this.logEventRepository.findAll();
        List<LogEventEntity> duplicates = eventEntities.stream()
                .collect(Collectors.groupingBy(LogEventEntity::getEventId, Collectors.toList()))
                .values()
                .stream()
                .filter(i -> i.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        logger.debug("Total  :: {}", duplicates.size());
        printLogEventData(eventEntities);
    }

    public void finaEventById(String eventId) {
        this.logEventRepository.findLogEventByEventId(eventId).ifPresent(this::printLogEventData);
    }

    public void finaEventByAlert(Boolean eventAlert) {
        List<LogEventEntity> eventEntities = this.logEventRepository.findLogEventByEventAlert(eventAlert);
        printLogEventData(eventEntities);
    }

    private void printLogEventData(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonData = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            final String filename = UUID.randomUUID().toString().replace("-", "") + ".txt";
            logger.info("Output data written to file : {}", filename);
            Files.write(Paths.get(filename), jsonData.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException jsonProcessingException) {
            logger.error("JsonProcessingException : {}", jsonProcessingException.getMessage());
        } catch (IOException ioException) {
            logger.error("IOException : {}", ioException.getMessage());
        } catch (Exception exception) {
            logger.error("Internal Error : {}", exception.getMessage());
        }
    }


}
