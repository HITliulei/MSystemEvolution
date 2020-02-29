package com.septemberhx.server.client;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.server.MServiceCompareBean;
import com.septemberhx.common.bean.server.MServiceRegisterBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.diff.MServiceDiff;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/1
 */
@FeignClient(name = MConfig.SERVICE_NAME_ANALYZE)
public interface MAnalyzerClient {
    @RequestMapping(value = MConfig.ANALYZE_ANALYZE_URI, method = RequestMethod.POST)
    MResponse analyzer(@RequestBody MServiceRegisterBean registerBean);

    @RequestMapping(value = MConfig.ANALYZE_COMPARE_URI, method = RequestMethod.POST)
    MServiceDiff compare(@RequestBody MServiceCompareBean compareBean);
}
