package com.isc.assignment.cms.service.impl;

import com.isc.assignment.cms.service.api.MemoryManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MemoryManagementServiceImpl implements MemoryManagementService {

    private final Logger logger = LoggerFactory.getLogger(MemoryManagementServiceImpl.class);

    @Scheduled(cron = "${memory.usage.pattern}")
    @Override
    public void printMemoryUsage() {

        int mb = 1024 * 1024;
        Runtime instance = Runtime.getRuntime();
        logger.info("***** Heap utilization statistics [MB] *****\n");
        logger.info("Total Memory: {}", instance.totalMemory() / mb);
        logger.info("Free Memory: {}", instance.freeMemory() / mb);
        logger.info("Used Memory: {}", (instance.totalMemory() - instance.freeMemory()) / mb);
        logger.info("Max Memory: {}", instance.maxMemory() / mb);
    }
}
