package com.septemberhx.mgateway.core;

import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/10
 */
@Component
public class MRequestProcessorThread implements Runnable {

    private final static Logger logger = LogManager.getLogger(MRequestProcessorThread.class);
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public void run() {
        try {
            while (true) {
                MDepRequestCacheBean nextRequest = MGatewayInfo.inst().getNextRequestBlocking();
                executorService.submit(() -> {
                    if (!BeanContext.getApplicationContext().getBean(MGatewayRequest.class).solveUserDepRequest(nextRequest)) {
                        logger.error(String.format(
                                "Thread-%s: Failed to process request from user %s with dependency %s accepted in %d [%s].",
                                Thread.currentThread().getName(),
                                nextRequest.getClientId(),
                                nextRequest.getBaseSvcDependency().getId(),
                                nextRequest.getTimestamp(),
                                new DateTime(nextRequest.getTimestamp()).toString()
                        ));
                    }
                });
            }
        } catch (InterruptedException e) {
            logger.debug(e);
        }
    }
}
