package com.septemberhx.server.client;

import com.septemberhx.common.bean.server.MServiceCompareBean;
import com.septemberhx.common.bean.server.MServiceRegisterBean;
import com.septemberhx.common.config.MClusterConfig;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MServiceDiff;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/1
 */
@FeignClient(name = MClusterConfig.SERVICE_NAME_ANALYZE)
public interface MAnalyzerClient {
    @RequestMapping(value = MClusterConfig.ANALYZE_ANALYZE_URI, method = RequestMethod.POST)
    List<MService> analyzer(@RequestBody MServiceRegisterBean registerBean);

    @RequestMapping(value = MClusterConfig.ANALYZE_COMPARE_URI, method = RequestMethod.POST)
    MServiceDiff compare(@RequestBody MServiceCompareBean compareBean);
}
