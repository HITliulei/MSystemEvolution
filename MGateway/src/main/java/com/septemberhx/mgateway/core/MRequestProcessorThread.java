package com.septemberhx.mgateway.core;

import com.septemberhx.common.bean.gateway.MDepRequestCacheBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/10
 */
public class MRequestProcessorThread implements Runnable {

    private final static Logger logger = LogManager.getLogger(MRequestProcessorThread.class);

    @Override
    public void run() {
        try {
            MDepRequestCacheBean nextRequest = null;
            while (true) {
                nextRequest = MGatewayInfo.inst().getNextRequestBlocking();

                // todo: thread pool to process user requests
                logger.info(nextRequest);
            }
        } catch (InterruptedException e) {
            logger.debug(e);
        }
    }
}
